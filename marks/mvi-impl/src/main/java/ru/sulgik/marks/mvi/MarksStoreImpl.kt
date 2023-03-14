package ru.sulgik.marks.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDate
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
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
    Store<MarksStore.Intent, MarksStore.State, MarksStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "MarksStoreImpl",
        initialState = MarksStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            val cache = mutableMapOf<MarksStore.State.Period, MarksStore.State.Marks>()
            onAction<Action.Setup> {
                launch {
                    val periods = cachedPeriodsRepository.getPeriodsFast(auth).toState()
                    val currentDate = java.time.LocalDate.now().toKotlinLocalDate()
                    val currentPeriod =
                        periods.firstOrNull { currentDate in it.period } ?: periods.first()
                    syncDispatch(
                        MarksStore.State(
                            periods = MarksStore.State.Periods(
                                isLoading = false,
                                data = MarksStore.State.PeriodsData(
                                    selectedPeriod = currentPeriod,
                                    periods = periods,
                                ),
                            )
                        )
                    )
                    var marks = cachedMarksRepository.getMarksFast(auth, currentPeriod.period)
                        .toState(this@onAction.state.marks)
                    cache[currentPeriod] = marks
                    syncDispatch(
                        state.copy(
                            marks = marks,
                        )
                    )
                    marks = cachedMarksRepository.getMarksActual(auth, currentPeriod.period)
                        .toState(this@onAction.state.marks)
                    cache[currentPeriod] = marks
                    syncDispatch(
                        state.copy(
                            marks = marks,
                        )
                    )
                }
            }
            var selectPeriodJob: Job? = null
            onIntent<MarksStore.Intent.SelectPeriod> {
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        marks = MarksStore.State.Marks(),
                        periods = state.periods.copy(
                            isLoading = false,
                            data = periodsData.copy(
                                selectedPeriod = it.period
                            )
                        )
                    )
                )
                val cachedMarks = cache[it.period]
                if (cachedMarks != null) {
                    dispatch(
                        this@onIntent.state.copy(
                            marks = cachedMarks,
                        )
                    )
                }
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    var marks = cachedMarksRepository.getMarksFast(auth, it.period.period)
                        .toState(this@onIntent.state.marks)
                    cache[it.period] = marks
                    syncDispatch(
                        this@onIntent.state.copy(
                            marks = marks,
                        )
                    )
                    marks = cachedMarksRepository.getMarksActual(auth, it.period.period)
                        .toState(this@onIntent.state.marks)
                    cache[it.period] = marks
                    syncDispatch(
                        this@onIntent.state.copy(
                            marks = marks,
                        )
                    )
                }
            }
            onIntent<MarksStore.Intent.HideMark> {
                val state = state
                val marksData = state.marks.data
                if (marksData == null) {
                    return@onIntent
                }
                dispatch(
                    state.copy(
                        marks = state.marks.copy(
                            data = marksData.copy(
                                selectedMark = null,
                            )
                        )
                    )
                )
            }
            onIntent<MarksStore.Intent.SelectMark> {
                val state = state
                val marksData = state.marks.data
                if (marksData == null) {
                    return@onIntent
                }
                dispatch(
                    state.copy(
                        marks = state.marks.copy(
                            data = marksData.copy(
                                selectedMark = it.mark
                            )
                        )
                    )
                )
            }
            onIntent<MarksStore.Intent.RefreshMarks> {
                val state = state
                val periodsData = state.periods.data
                if (state.periods.isLoading || periodsData == null || state.marks.isLoading || state.marks.isRefreshing)
                    return@onIntent
                val period = periodsData.selectedPeriod
                dispatch(
                    state.copy(
                        marks = state.marks.copy(
                            isRefreshing = true
                        ),
                        periods = state.periods.copy(
                            isLoading = false,
                            data = periodsData.copy(
                                selectedPeriod = period
                            )
                        )
                    )
                )
                val job = selectPeriodJob
                selectPeriodJob = launch {
                    job?.cancelAndJoin()
                    val marks = cachedMarksRepository.getMarksActual(auth, period.period)
                        .toState(this@onIntent.state.marks)
                    cache[period] = marks
                    syncDispatch(
                        this@onIntent.state.copy(
                            marks = marks,
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

private fun GetPeriodsOutput.toState(): List<MarksStore.State.Period> {
    return periods.map {
        MarksStore.State.Period(
            it.title,
            it.period,
        )
    }
}

private fun MarksOutput.toState(
    state: MarksStore.State.Marks,
    isRefreshing: Boolean = false,
): MarksStore.State.Marks {
    return state.copy(
        isLoading = false,
        isRefreshing = isRefreshing,
        data = state.data?.copy(
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
                    }
                )
            },
        ) ?: MarksStore.State.MarksData(
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
                    }
                )
            },
            selectedMark = null,
        ),
    )
}
