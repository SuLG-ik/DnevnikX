package ru.sulgik.dnevnikx.mvi.finalmarks

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.repository.data.FinalMarksOutput
import ru.sulgik.dnevnikx.repository.finalmarks.CachedFinalMarksRepository

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [FinalMarksStore::class])
class FinalMarksStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedFinalMarksRepository: CachedFinalMarksRepository,
) : FinalMarksStore,
    Store<FinalMarksStore.Intent, FinalMarksStore.State, FinalMarksStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "FinalMarksStoreImpl",
        initialState = FinalMarksStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    var lessons = cachedFinalMarksRepository.getFinalMarksFast(authScope)
                    syncDispatch(
                        state.copy(
                            isLoading = false,
                            lessons = lessons.toState()
                        )
                    )
                    lessons = cachedFinalMarksRepository.getFinalMarksActual(authScope)
                    syncDispatch(
                        state.copy(
                            isLoading = false,
                            lessons = lessons.toState()
                        )
                    )
                }
            }
            var refreshJob: Job? = null
            onIntent<FinalMarksStore.Intent.RefreshFinalMarks> {
                val state = state
                if (state.isLoading || state.isRefreshing)
                    return@onIntent
                dispatch(state.copy(isRefreshing = true))
                val job = refreshJob
                refreshJob = launch {
                    job?.cancelAndJoin()
                    val lessons = cachedFinalMarksRepository.getFinalMarksActual(authScope)
                    syncDispatch(
                        this@onIntent.state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            lessons = lessons.toState()
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

private fun FinalMarksOutput.toState(): FinalMarksStore.State.LessonsData {
    return FinalMarksStore.State.LessonsData(
        lessons.map { lesson ->
            FinalMarksStore.State.Lesson(
                title = lesson.title.ifBlank { "-" },
                marks = lesson.marks.map { mark ->
                    FinalMarksStore.State.Mark(
                        mark = mark.mark.ifBlank { "-" },
                        value = mark.value,
                        period = mark.period
                    )
                }
            )
        }
    )
}
