package ru.sulgik.marksupdates

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.sulgik.core.directReducer
import ru.sulgik.marksupdates.domain.PagingRemoteMarksUpdateSource
import ru.sulgik.marksupdates.domain.data.MarksUpdatesOutput
import ru.sulgik.marksupdates.domain.data.PagingData
import ru.sulgik.marksupdates.mvi.MarksUpdatesStore


@OptIn(ExperimentalMviKotlinApi::class)
class MarksUpdatesStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    pagingRemoteMarksUpdateSource: PagingRemoteMarksUpdateSource,
) : MarksUpdatesStore,
    Store<MarksUpdatesStore.Intent, MarksUpdatesStore.State, MarksUpdatesStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "MarksUpdatesStoreImpl",
        initialState = MarksUpdatesStore.State(
            pagingRemoteMarksUpdateSource.data.value.toState(),
        ),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                pagingRemoteMarksUpdateSource.data.onEach {
                    dispatch(
                        state.copy(
                            updates = it.toState()
                        )
                    )
                }.flowOn(Dispatchers.Main).launchIn(this)
                launch {
                    pagingRemoteMarksUpdateSource.loadNextPage(2)
                }
            }
            onIntent<MarksUpdatesStore.Intent.LoadNextPage> {
                launch {
                    pagingRemoteMarksUpdateSource.loadNextPage()
                }
            }
            onIntent<MarksUpdatesStore.Intent.Refresh> {
                launch {
                    pagingRemoteMarksUpdateSource.refreshPage()
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}

private fun PagingData<MarksUpdatesOutput>.toState(): MarksUpdatesStore.State.MarksUpdates {
    return MarksUpdatesStore.State.MarksUpdates(
        isLoading = isLoading,
        data = data?.toState(isRefreshing, isNextPageLoading),
    )
}

private fun MarksUpdatesOutput.toState(
    isRefreshing: Boolean,
    isNextPageLoading: Boolean,
): MarksUpdatesStore.State.MarksUpdatesData {
    return MarksUpdatesStore.State.MarksUpdatesData(
        isRefreshing = isRefreshing,
        latest = MarksUpdatesStore.State.MarksUpdatesPeriodData(
            isNextPageLoading = isNextPageLoading && old.isEmpty(),
            latest.map {
                it.toState()
            }.toPersistentList()
        ),
        old = MarksUpdatesStore.State.MarksUpdatesPeriodData(
            isNextPageLoading = isNextPageLoading && old.isNotEmpty(),
            old.map {
                it.toState()
            }.toPersistentList()
        ),
    )
}


private fun MarksUpdatesOutput.MarkUpdate.toState(): MarksUpdatesStore.State.MarkUpdate {
    return MarksUpdatesStore.State.MarkUpdate(
        lesson = MarksUpdatesStore.State.Lesson(
            name = lesson.name,
            date = lesson.date,
        ),
        current = MarksUpdatesStore.State.Mark(
            mark = currentMark.mark,
            value = currentMark.value,
        ),
        previous = previousMark?.let {
            MarksUpdatesStore.State.Mark(
                mark = it.mark,
                value = it.value,
            )
        },
    )
}
