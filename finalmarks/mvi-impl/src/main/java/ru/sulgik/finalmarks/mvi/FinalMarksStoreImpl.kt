package ru.sulgik.finalmarks.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.CachedFinalMarksRepository
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput
import ru.sulgik.kacher.core.on

@OptIn(ExperimentalMviKotlinApi::class)
class FinalMarksStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedFinalMarksRepository: CachedFinalMarksRepository,
) : FinalMarksStore,
    Store<FinalMarksStore.Intent, FinalMarksStore.State, FinalMarksStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "FinalMarksStoreImpl",
        initialState = FinalMarksStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                val request = cachedFinalMarksRepository.getFinalMarks(authScope)
                launch(Dispatchers.Main) {
                    request.on(
                        statusUpdated = { status ->
                            status.data?.let {
                                dispatch(Message.SetData(it.toState()))
                            }
                        }
                    )
                }
            }
            onIntent<FinalMarksStore.Intent.RefreshFinalMarks> {
                val state = state
                val stateLessons = state.lessons
                if (state.isLoading || stateLessons?.isRefreshing == true)
                    return@onIntent
                dispatch(Message.SetRefreshing(true))
                val request = cachedFinalMarksRepository.getFinalMarksActual(authScope)

                launch(Dispatchers.Main) {
                    request.on(
                        statusUpdated = { status ->
                            status.data?.let {
                                dispatch(Message.SetData(it.toState()))
                            }
                        }
                    )
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SetData -> copy(
                    isLoading = false,
                    lessons = FinalMarksStore.State.LessonsData(
                        isRefreshing = false,
                        lessons = it.lessons,
                    )
                )

                is Message.SetRefreshing -> copy(
                    isLoading = !it.value,
                    lessons = lessons?.copy(
                        isRefreshing = it.value,
                    ) ?: FinalMarksStore.State.LessonsData(isRefreshing = it.value)
                )
            }
        },
    ) {

    private sealed interface Action {
        object Setup : Action
    }

    private sealed interface Message {

        data class SetRefreshing(val value: Boolean) : Message

        data class SetData(val lessons: ImmutableList<FinalMarksStore.State.Lesson>) : Message

    }

}

private fun FinalMarksOutput.toState(): PersistentList<FinalMarksStore.State.Lesson> {
    return lessons.map { lesson ->
        FinalMarksStore.State.Lesson(
            title = lesson.title.ifBlank { "-" },
            marks = lesson.marks.map { mark ->
                FinalMarksStore.State.Mark(
                    mark = mark.mark.ifBlank { "-" },
                    value = mark.value,
                    period = mark.period
                )
            }.toPersistentList()
        )
    }.toPersistentList()
}
