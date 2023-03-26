package ru.sulgik.application.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Factory
import ru.sulgik.core.directReducer

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [ApplicationStore::class])
class ApplicationStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
) : ApplicationStore,
    Store<ApplicationStore.Intent, ApplicationStore.State, ApplicationStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ApplicationStoreImpl",
        initialState = ApplicationStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {

            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}