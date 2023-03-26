package ru.sulgik.experimentalsettings.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import ru.sulgik.core.directReducer

@OptIn(ExperimentalMviKotlinApi::class)
class ExperimentalSettingsStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
) : ExperimentalSettingsStore,
    Store<ExperimentalSettingsStore.Intent, ExperimentalSettingsStore.State, ExperimentalSettingsStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ExperimentalSettingsStoreImpl",
        initialState = ExperimentalSettingsStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}
