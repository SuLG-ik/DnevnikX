package ru.sulgik.application.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Factory
import ru.sulgik.application.settings.NestedScreenTransitionSetting
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.settings.provider.SettingsProvider
import ru.sulgik.settings.provider.getSettingFlow

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [ApplicationStore::class])
class ApplicationStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    settingsProvider: SettingsProvider,
) : ApplicationStore,
    Store<ApplicationStore.Intent, ApplicationStore.State, ApplicationStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ApplicationStoreImpl",
        initialState = ApplicationStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                settingsProvider.getSettingFlow<NestedScreenTransitionSetting>(authScope)
                    .distinctUntilChanged().onEach {
                        dispatch(state.copy(state.applicationConfig.copy(it.enabled)))
                    }.flowOn(Dispatchers.Main).launchIn(this)
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}