package ru.sulgik.dnevnikx.mvi.marks

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
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput
import ru.sulgik.dnevnikx.repository.data.MarksOutput
import ru.sulgik.dnevnikx.repository.marks.RemoteMarksRepository
import ru.sulgik.dnevnikx.repository.periods.RemotePeriodsRepository


@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [MarksStore::class])
class MarksStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    auth: AuthScope,
    remotePeriodsRepository: RemotePeriodsRepository,
    remoteMarksRepository: RemoteMarksRepository,
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
                    val periods = remotePeriodsRepository.getPeriods(auth).toState()
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
                    val marks = remoteMarksRepository.getMarks(auth, currentPeriod.period).toState(this@onAction.state.marks)
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
                if (state.periods.isLoading || state.periods.data == null)
                    return@onIntent
                dispatch(
                    state.copy(
                        marks = MarksStore.State.Marks(),
                        periods = state.periods.copy(
                            isLoading = false,
                            data = state.periods.data.copy(
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
                    val marks = remoteMarksRepository.getMarks(auth, it.period.period)
                        .toState(this@onIntent.state.marks)
                    cache[it.period] = marks
                    syncDispatch(
                        this@onIntent.state.copy(
                            marks = marks,
                        )
                    )
                }
            }
            onIntent<MarksStore.Intent.SelectMark> {
                val state = state
                if (state.marks.data == null) {
                    return@onIntent
                }
                dispatch(
                    state.copy(
                        marks = state.marks.copy(
                            data = state.marks.data.copy(
                                selectedMark = it.mark
                            )
                        )
                    )
                )
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

private fun MarksOutput.toState(state: MarksStore.State.Marks): MarksStore.State.Marks {
    return state.copy(
        isLoading = false,
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
