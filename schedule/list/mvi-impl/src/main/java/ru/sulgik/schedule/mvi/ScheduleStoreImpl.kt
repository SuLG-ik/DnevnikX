package ru.sulgik.schedule.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import ru.sulgik.account.domain.RemoteAccountRepository
import ru.sulgik.account.domain.data.GetAccountOutput
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.core.syncDispatch
import ru.sulgik.kacher.core.on
import ru.sulgik.periods.domain.CachedPeriodsRepository
import ru.sulgik.periods.domain.data.GetPeriodsOutput
import ru.sulgik.schedule.domain.RemoteScheduleRepository
import ru.sulgik.schedule.domain.data.GetScheduleOutput
import java.time.LocalDate

@OptIn(ExperimentalMviKotlinApi::class)
class ScheduleStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    remoteAccountRepository: RemoteAccountRepository,
    cachedPeriodsRepository: CachedPeriodsRepository,
    remoteScheduleRepository: RemoteScheduleRepository,
) : ScheduleStore,
    Store<ScheduleStore.Intent, ScheduleStore.State, ScheduleStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "ScheduleStoreImpl",
        initialState = ScheduleStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            var cachedAccount: GetAccountOutput? = null
            onAction<Action.Setup> {
                launch(Dispatchers.Main) {
                    val periodsRequest = cachedPeriodsRepository.getPeriods(authScope)
                    periodsRequest.on(
                        statusUpdated = { status ->
                            status.data?.periods?.toState()?.let { periods ->
                                syncDispatch(Message.UpdatePeriods(periods))
                                val account = remoteAccountRepository.getAccount(authScope)
                                cachedAccount = account
                                val schedule = remoteScheduleRepository.getSchedule(
                                    auth = authScope,
                                    period = periods.selectedPeriod,
                                    classGroup = account.student.classGroup.title
                                ).toState()
                                syncDispatch(
                                    Message.UpdateSchedule(
                                        periods.selectedPeriod,
                                        schedule
                                    )
                                )
                            }
                        }
                    )
                }
            }

            onIntent<ScheduleStore.Intent.SelectOtherPeriod> {
                dispatch(Message.ShowOtherPeriods(true))
            }
            onIntent<ScheduleStore.Intent.HidePeriodSelector> {
                dispatch(Message.ShowOtherPeriods(false))
            }
            var selectPeriodJob: Job? = null
            onIntent<ScheduleStore.Intent.SelectPeriod> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                dispatch(Message.SelectPeriod(intent.period))
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    val account = cachedAccount ?: remoteAccountRepository.getAccount(authScope)
                        .also { cachedAccount = it }
                    job?.cancelAndJoin()
                    val schedule = remoteScheduleRepository.getSchedule(
                        auth = authScope,
                        period = intent.period,
                        classGroup = account.student.classGroup.title
                    ).toState()
                    syncDispatch(Message.UpdateSchedule(intent.period, schedule))
                }
            }
            onIntent<ScheduleStore.Intent.RefreshSchedule> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                val scheduleData = state.schedule.data[intent.period]
                if (scheduleData?.isLoading == true || scheduleData?.isRefreshing == true) return@onIntent
                dispatch(Message.SetScheduleRefreshing(intent.period))
                val period = periodsData.selectedPeriod
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    val account = cachedAccount ?: remoteAccountRepository.getAccount(authScope)
                        .also { cachedAccount = it }
                    job?.cancelAndJoin()
                    val schedule = remoteScheduleRepository.getSchedule(
                        auth = authScope,
                        period = period,
                        classGroup = account.student.classGroup.title
                    ).toState()
                    syncDispatch(Message.UpdateSchedule(intent.period, schedule))
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SelectPeriod -> {
                    copy(
                        periods = periods.copy(
                            data = periods.data?.copy(
                                selectedPeriod = it.period,
                            )
                        )
                    )
                }

                is Message.SetScheduleRefreshing -> {
                    copy(
                        schedule = schedule.copy(
                            data = schedule.data.orFill(it.period, isRefreshing = true)
                        )
                    )
                }

                is Message.ShowOtherPeriods -> copy(
                    periods = periods.copy(data = periods.data?.copy(isOther = it.value))
                )

                is Message.UpdatePeriods -> copy(
                    periods = ScheduleStore.State.Periods(
                        isLoading = false, data = it.periods
                    ),
                )

                is Message.UpdateSchedule -> copy(
                    schedule = it.schedule.withState(it.period, this),
                )
            }
        },
    ) {

    private sealed interface Message {

        data class UpdatePeriods(val periods: ScheduleStore.State.PeriodsData) : Message

        data class ShowOtherPeriods(val value: Boolean) : Message

        data class SelectPeriod(val period: DatePeriod) : Message

        data class UpdateSchedule(
            val period: DatePeriod,
            val schedule: ScheduleStore.State.ScheduleData,
        ) : Message

        data class SetScheduleRefreshing(val period: DatePeriod) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun String.formatTeacherName(): String {
    if (isBlank()) {
        return this
    }
    val words = split(" ")
    if (words.size == 1) {
        return this
    }
    return buildString {
        append(words[0])
        append(" ")
        for (i in 1 until words.size) {
            append(words[i].first())
            append(". ")
        }
    }
}

private fun List<GetPeriodsOutput.Period>.toState(): ScheduleStore.State.PeriodsData {
    val periods = flatMap { it.nestedPeriods }
    val currentDate = LocalDate.now().toKotlinLocalDate()
    val currentPeriod = getPeriod(currentDate)
    val nextPeriod = getPeriod(currentDate.plus(1, DateTimeUnit.WEEK))
    val previousPeriod = getPeriod(currentDate.minus(1, DateTimeUnit.WEEK))
    return ScheduleStore.State.PeriodsData(
        currentPeriod = if (currentPeriod in periods) currentPeriod else null,
        nextPeriod = if (nextPeriod in periods) nextPeriod else null,
        previousPeriod = if (previousPeriod in periods) previousPeriod else null,
        selectedPeriod = currentPeriod,
        periods = periods.toPersistentList(),
        isOther = false,
    )
}

private fun ImmutableMap<DatePeriod, ScheduleStore.State.ScheduleData>?.orFill(
    period: DatePeriod,
    isRefreshing: Boolean = false,
): ImmutableMap<DatePeriod, ScheduleStore.State.ScheduleData> {
    return this?.toPersistentMap()?.mutate {
        val data = it[period]?.copy(isRefreshing = isRefreshing, isLoading = !isRefreshing)
            ?: ScheduleStore.State.ScheduleData(
                isLoading = true,
                isRefreshing = false,
                schedule = persistentListOf(),
            )
        it[period] = data
    } ?: persistentMapOf()
}

private fun ScheduleStore.State.ScheduleData.withState(
    period: DatePeriod,
    state: ScheduleStore.State,
): ScheduleStore.State.Schedule {
    return state.schedule.copy(data = state.schedule.data.let { mark ->
        mark.toPersistentMap().mutate {
            it[period] = this
        }
    })
}

private fun GetScheduleOutput.toState(): ScheduleStore.State.ScheduleData {
    return ScheduleStore.State.ScheduleData(
        isLoading = false,
        isRefreshing = false,
        schedule = schedule.map { item ->
            ScheduleStore.State.ScheduleDate(
                title = item.title,
                date = item.date,
                lessonGroups = item.lessonGroups.map { lessonGroup ->
                    ScheduleStore.State.LessonGroup(
                        number = lessonGroup.number,
                        lessons = lessonGroup.lessons.map { lesson ->
                            ScheduleStore.State.Lesson(
                                title = lesson.title,
                                time = lesson.time,
                                teacher = lesson.teacher.formatTeacherName(),
                                group = lesson.group
                            )
                        }.toPersistentList(),
                    )
                }.toPersistentList(),
            )
        }.toPersistentList(),
    )
}


fun getPeriod(date: kotlinx.datetime.LocalDate): DatePeriod {
    val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
    return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
}