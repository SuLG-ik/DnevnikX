package ru.sulgik.experimentalsettings.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.sulgik.application.settings.NestedScreenTransactionSetting
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.settings.provider.SettingsProvider
import ru.sulgik.settings.provider.getSettingFlow
import ru.sulgik.settings.provider.provide

@OptIn(ExperimentalMviKotlinApi::class)
class ExperimentalSettingsStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    settingsProvider: SettingsProvider,
) : ExperimentalSettingsStore,
    Store<ExperimentalSettingsStore.Intent, ExperimentalSettingsStore.State, ExperimentalSettingsStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ExperimentalSettingsStoreImpl",
        initialState = ExperimentalSettingsStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                settingsProvider.getSettingFlow<NestedScreenTransactionSetting>(authScope)
                    .onEach {
                        dispatch(
                            state.copy(
                                isLoading = false,
                                ExperimentalSettingsStore.State.SettingsData(
                                    ExperimentalSettingsStore.State.UISettings(
                                        isNestedScreenTransitionEnabled = it.enabled,
                                    ),
                                )
                            )
                        )
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(this)
            }
            onIntent<ExperimentalSettingsStore.Intent.ToggleNestedScreenTransition> {
                val state = state
                val settingsState = state.settings ?: return@onIntent
                dispatch(state.copy(settings = settingsState.copy(ui = settingsState.ui.copy(it.value))))
                launch {
                    settingsProvider.provide(authScope, NestedScreenTransactionSetting(it.value))
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}
