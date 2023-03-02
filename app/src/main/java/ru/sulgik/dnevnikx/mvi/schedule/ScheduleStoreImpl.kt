package ru.sulgik.dnevnikx.mvi.schedule

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toKotlinLocalDate
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.diary.getPeriod
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.account.RemoteAccountRepository
import ru.sulgik.dnevnikx.repository.data.GetAccountOutput
import ru.sulgik.dnevnikx.repository.data.GetScheduleOutput
import ru.sulgik.dnevnikx.repository.periods.CachedPeriodsRepository
import ru.sulgik.dnevnikx.repository.schedule.RemoteScheduleRepository
import java.time.LocalDate

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [ScheduleStore::class])
class ScheduleStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    remoteAccountRepository: RemoteAccountRepository,
    cachedPeriodsRepository: CachedPeriodsRepository,
    remoteScheduleRepository: RemoteScheduleRepository,
) : ScheduleStore,
    Store<ScheduleStore.Intent, ScheduleStore.State, ScheduleStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ScheduleStoreImpl",
        initialState = ScheduleStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            val cache = mutableMapOf<DatePeriod, ScheduleStore.State.Schedule>()
            var cachedAccount: GetAccountOutput? = null
            onAction<Action.Setup> {
                launch {
                    val account = remoteAccountRepository.getAccount(authScope)
                    cachedAccount = account
                    val periods =
                        cachedPeriodsRepository.getPeriodsFast(authScope).periods.flatMap { it.nestedPeriods }
                    val currentDate = LocalDate.now().toKotlinLocalDate()
                    val currentPeriod = getPeriod(currentDate)
                    val nextPeriod = getPeriod(currentDate.plus(1, DateTimeUnit.WEEK))
                    val previousPeriod = getPeriod(currentDate.minus(1, DateTimeUnit.WEEK))
                    syncDispatch(
                        ScheduleStore.State(
                            periods = ScheduleStore.State.Periods(
                                isLoading = false,
                                data = ScheduleStore.State.PeriodsData(
                                    currentPeriod = if (currentPeriod in periods) currentPeriod else null,
                                    nextPeriod = if (nextPeriod in periods) nextPeriod else null,
                                    previousPeriod = if (previousPeriod in periods) previousPeriod else null,
                                    selectedPeriod = currentPeriod,
                                    periods = periods,
                                    isOther = false,
                                ),
                            )
                        )
                    )
                    val schedule = remoteScheduleRepository.getSchedule(
                        auth = authScope,
                        period = currentPeriod,
                        classGroup = account.student.classGroup.title
                    ).toState()
                    cache[currentPeriod] = schedule
                    syncDispatch(
                        state.copy(
                            schedule = schedule,
                        )
                    )
                }
            }

            onIntent<ScheduleStore.Intent.SelectOtherPeriod> {
                val state = state
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        periods = state.periods.copy(
                            data = state.periods.data.copy(
                                isOther = true
                            )
                        )
                    )
                )
            }
            var selectPeriodJob: Job? = null
            onIntent<ScheduleStore.Intent.SelectPeriod> { intent ->
                val state = state
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        schedule = ScheduleStore.State.Schedule(),
                        periods = state.periods.copy(
                            data = state.periods.data.copy(
                                selectedPeriod = intent.period,
                                isOther = false,
                            ),
                        )
                    )
                )
                val cachedSchedule = cache[intent.period]
                if (cachedSchedule != null) {
                    dispatch(
                        this@onIntent.state.copy(
                            schedule = cachedSchedule,
                        )
                    )
                }
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
                    cache[intent.period] = schedule
                    syncDispatch(
                        this@onIntent.state.copy(
                            schedule = schedule,
                        )
                    )
                }
            }
            onIntent<ScheduleStore.Intent.RefreshSchedule> { intent ->
                val state = state
                if (state.periods.isLoading || state.periods.data == null || state.schedule.isLoading || state.schedule.isRefreshing)
                    return@onIntent
                dispatch(
                    state.copy(
                        schedule = state.schedule.copy(
                            isRefreshing = true,
                        ),
                    )
                )
                val period = state.periods.data.selectedPeriod
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
                    cache[period] = schedule
                    syncDispatch(
                        this@onIntent.state.copy(
                            schedule = schedule,
                        )
                    )
                }
            }
        },
        reducer = directReducer(),
    ) {

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

private fun GetScheduleOutput.toState(): ScheduleStore.State.Schedule {
    return ScheduleStore.State.Schedule(
        isLoading = false,
        schedule = ScheduleStore.State.ScheduleData(
            schedule.map { item ->
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
                            }
                        )
                    }
                )
            },
        )
    )
}
