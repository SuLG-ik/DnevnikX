package ru.sulgik.marks.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.on
import ru.sulgik.marks.domain.CachedMarksRepository
import ru.sulgik.marks.domain.data.MarksOutput
import ru.sulgik.periods.domain.CachedPeriodsRepository
import ru.sulgik.periods.domain.data.GetPeriodsOutput


@OptIn(ExperimentalMviKotlinApi::class)
class MarksStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    auth: AuthScope,
    cachedPeriodsRepository: CachedPeriodsRepository,
    cachedMarksRepository: CachedMarksRepository,
) : MarksStore,
    Store<MarksStore.Intent, MarksStore.State, MarksStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "MarksStoreImpl",
        initialState = MarksStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            var selectPeriodJob: Job? = null
            onAction<Action.Setup> {
                val periodsRequest = cachedPeriodsRepository.getPeriods(auth)
                launch(Dispatchers.Main) {
                    periodsRequest.on(
                        statusUpdated = { periodsStatus ->
                            periodsStatus.data?.toState()?.let { periods ->
                                dispatch(Message.UpdatePeriods(periods))
                                cachedMarksRepository.getMarks(
                                    auth = auth,
                                    period = MarksOutput.Period(
                                        periods.selectedPeriod.title,
                                        periods.selectedPeriod.period
                                    )
                                ).on(
                                    statusUpdated = { diaryStatus ->
                                        diaryStatus.data?.let {
                                            dispatch(
                                                Message.UpdateMarks(
                                                    periods.selectedPeriod,
                                                    it.toState(),
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        },
                    )
                }
            }
            onIntent<MarksStore.Intent.SelectPeriod> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null || periodsData.selectedPeriod == intent.period) return@onIntent
                dispatch(
                    Message.SelectPeriod(period = intent.period)
                )
                val marksRequest =
                    cachedMarksRepository.getMarks(
                        auth,
                        MarksOutput.Period(intent.period.title, intent.period.period)
                    )
                val job = selectPeriodJob
                selectPeriodJob = launch(Dispatchers.Main) {
                    job?.cancelAndJoin()
                    marksRequest.on(
                        statusUpdated = { diaryStatus ->
                            diaryStatus.data?.let {
                                dispatch(
                                    Message.UpdateMarks(
                                        intent.period,
                                        it.toState(),
                                    )
                                )
                            }
                        }
                    )
                }
            }
            onIntent<MarksStore.Intent.HideMark> {
                dispatch(
                    Message.SelectMark(null)
                )
            }
            onIntent<MarksStore.Intent.SelectMark> {
                dispatch(
                    Message.SelectMark(MarksStore.State.SelectedMark(it.mark.first, it.mark.second))
                )
            }
            onIntent<MarksStore.Intent.RefreshMarks> { intent ->
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null) return@onIntent
                val diaryData = state.marks.data[intent.period]
                if (diaryData?.isLoading == true || diaryData?.isRefreshing == true) return@onIntent
                dispatch(
                    Message.SetDiaryRefreshing(intent.period)
                )
                val marksRequest =
                    cachedMarksRepository.getMarksActual(
                        auth,
                        MarksOutput.Period(intent.period.title, intent.period.period)
                    )
                val job = selectPeriodJob
                selectPeriodJob = launch(Dispatchers.Main) {
                    job?.cancelAndJoin()
                    marksRequest.on(
                        statusUpdated = { diaryStatus ->
                            diaryStatus.data?.let {
                                dispatch(
                                    Message.UpdateMarks(
                                        intent.period,
                                        it.toState(),
                                    )
                                )
                            }
                        }
                    )
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SelectMark -> copy(
                    marks = marks.copy(
                        selectedMark = it.lesson
                    )
                )

                is Message.SelectPeriod -> {
                    copy(
                        periods = periods.copy(
                            data = periods.data?.copy(
                                selectedPeriod = it.period,
                            )
                        )
                    )
                }

                is Message.SetDiaryRefreshing -> {
                    copy(
                        marks = marks.copy(
                            data = marks.data.orFill(it.period, isRefreshing = true)
                        )
                    )
                }

                is Message.UpdateMarks -> copy(
                    marks = it.marks.withState(it.period, this),
                )

                is Message.UpdatePeriods -> copy(
                    periods = MarksStore.State.Periods(
                        isLoading = false, data = it.periods
                    ),
                )

            }
        },
    ) {

    private sealed interface Message {

        data class UpdatePeriods(val periods: MarksStore.State.PeriodsData) : Message

        data class SelectPeriod(val period: MarksStore.State.Period) : Message

        data class UpdateMarks(
            val period: MarksStore.State.Period,
            val marks: MarksStore.State.MarksLesson,
        ) : Message

        data class SetDiaryRefreshing(val period: MarksStore.State.Period) : Message

        data class SelectMark(val lesson: MarksStore.State.SelectedMark?) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun ImmutableMap<MarksStore.State.Period, MarksStore.State.MarksLesson>?.orFill(
    period: MarksStore.State.Period,
    isRefreshing: Boolean = false,
): ImmutableMap<MarksStore.State.Period, MarksStore.State.MarksLesson> {
    return this?.toPersistentMap()?.mutate {
        val data = it[period]?.copy(isRefreshing = isRefreshing, isLoading = !isRefreshing)
            ?: MarksStore.State.MarksLesson(
                isLoading = true,
                isRefreshing = false,
                lessons = persistentListOf(),
            )
        it[period] = data
    } ?: persistentMapOf()
}


private fun MarksStore.State.MarksLesson.withState(
    period: MarksStore.State.Period,
    state: MarksStore.State,
): MarksStore.State.Marks {
    return state.marks.copy(data = state.marks.data.let { mark ->
        mark.toPersistentMap().mutate {
            it[period] = this
        }
    })
}


private fun GetPeriodsOutput.toState(): MarksStore.State.PeriodsData {
    val currentDate = java.time.LocalDate.now().toKotlinLocalDate()
    val currentPeriod =
        periods.firstOrNull { currentDate in it.period } ?: periods.first()
    return MarksStore.State.PeriodsData(
        currentPeriod.toState(),
        periods.map { it.toState() },
    )
}


private fun GetPeriodsOutput.Period.toState(): MarksStore.State.Period {
    return MarksStore.State.Period(title, period)
}


private fun MarksOutput.toState(): MarksStore.State.MarksLesson {
    return MarksStore.State.MarksLesson(
        isLoading = false,
        isRefreshing = false,
        lessons = lessons.map { lesson ->
            MarksStore.State.Lesson(
                title = lesson.title,
                average = lesson.average,
                averageValue = lesson.averageValue,
                marks = lesson.marks.map { mark ->
                    MarksStore.State.Mark(
                        mark = mark.mark,
                        value = mark.value,
                        date = mark.date,
                        message = mark.message
                    )
                })
        },
    )

}
