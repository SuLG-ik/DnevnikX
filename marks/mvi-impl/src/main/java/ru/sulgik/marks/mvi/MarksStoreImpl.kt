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
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.syncDispatch
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
            onAction<Action.Setup> {
                launch {
                    val periods = cachedPeriodsRepository.getPeriodsFast(auth).toState()
                    val currentDate = java.time.LocalDate.now().toKotlinLocalDate()
                    val currentPeriod =
                        periods.firstOrNull { currentDate in it.period } ?: periods.first()
                    syncDispatch(
                        Message.UpdatePeriods(
                            periods = MarksStore.State.PeriodsData(
                                selectedPeriod = currentPeriod,
                                periods = periods,
                            )
                        )
                    )
                    var marks =
                        cachedMarksRepository.getMarksFast(auth, currentPeriod.period).toState()
                    syncDispatch(
                        Message.UpdateMarks(
                            period = currentPeriod,
                            marks = marks,
                        )
                    )
                    marks =
                        cachedMarksRepository.getMarksActual(auth, currentPeriod.period).toState()
                    syncDispatch(
                        Message.UpdateMarks(
                            period = currentPeriod,
                            marks = marks,
                        )
                    )
                }
            }
            var selectPeriodJob: Job? = null
            onIntent<MarksStore.Intent.SelectPeriod> {
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null) return@onIntent
                dispatch(
                    Message.SelectPeriod(period = it.period)
                )
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    var marks = cachedMarksRepository.getMarksFast(auth, it.period.period).toState()
                    syncDispatch(
                        Message.UpdateMarks(
                            period = it.period,
                            marks = marks,
                        )
                    )
                    marks = cachedMarksRepository.getMarksActual(auth, it.period.period).toState()
                    syncDispatch(
                        Message.UpdateMarks(
                            period = it.period,
                            marks = marks,
                        )
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
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    val marks =
                        cachedMarksRepository.getMarksActual(auth, intent.period.period).toState()
                    syncDispatch(
                        Message.UpdateMarks(intent.period, marks)
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
            val marks: MarksStore.State.MarksData,
        ) : Message

        data class SetDiaryRefreshing(val period: MarksStore.State.Period) : Message

        data class SelectMark(val lesson: MarksStore.State.SelectedMark?) : Message

    }

    private sealed interface Action {
        object Setup : Action
    }

}

private fun ImmutableMap<MarksStore.State.Period, MarksStore.State.MarksData>?.orFill(
    period: MarksStore.State.Period,
    isRefreshing: Boolean = false,
): ImmutableMap<MarksStore.State.Period, MarksStore.State.MarksData> {
    return this?.toPersistentMap()?.mutate {
        val data = it[period]?.copy(isRefreshing = isRefreshing, isLoading = !isRefreshing)
            ?: MarksStore.State.MarksData(
                isLoading = true,
                isRefreshing = false,
                lessons = persistentListOf(),
            )
        it[period] = data
    } ?: persistentMapOf()
}

private fun GetPeriodsOutput.toState(): List<MarksStore.State.Period> {
    return periods.map {
        MarksStore.State.Period(
            it.title,
            it.period,
        )
    }
}


private fun MarksStore.State.MarksData.withState(
    period: MarksStore.State.Period,
    state: MarksStore.State,
): MarksStore.State.Marks {
    return state.marks.copy(data = state.marks.data.let { mark ->
        mark.toPersistentMap().mutate {
            it[period] = this
        }
    })
}


private fun MarksOutput.toState(): MarksStore.State.MarksData {
    return MarksStore.State.MarksData(
        isLoading = false,
        isRefreshing = false,
        lessons = lessons.map { lesson ->
            MarksStore.State.Lesson(title = lesson.title,
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
