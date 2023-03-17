package ru.sulgik.experimentalsettings.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import ru.sulgik.application.settings.NestedScreenTransitionSetting
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.diary.settings.DiaryPagerEnabledSetting
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
                val nestedScreenTransition =
                    settingsProvider.getSettingFlow<NestedScreenTransitionSetting>(authScope)
                val diaryPager =
                    settingsProvider.getSettingFlow<DiaryPagerEnabledSetting>(authScope)
                nestedScreenTransition.combine(diaryPager) { transition, diary ->
                    dispatch(
                        state.copy(
                            isLoading = false,
                            settings = ExperimentalSettingsStore.State.SettingsData(
                                ui = ExperimentalSettingsStore.State.UISettings(
                                    isNestedScreenTransitionEnabled = transition.enabled,
                                ),
                                diary = ExperimentalSettingsStore.State.DiarySettings(
                                    isPagerEnabled = diary.enabled,
                                )
                            )
                        )
                    )
                }.flowOn(Dispatchers.Main).launchIn(this)
            }
            onIntent<ExperimentalSettingsStore.Intent.ToggleNestedScreenTransition> {
                val state = state
                val settingsState = state.settings ?: return@onIntent
                dispatch(state.copy(settings = settingsState.copy(ui = settingsState.ui.copy(it.value))))
                launch {
                    settingsProvider.provide(authScope, NestedScreenTransitionSetting(it.value))
                }
            }
            onIntent<ExperimentalSettingsStore.Intent.ToggleDiaryPager> {
                val state = state
                val settingsState = state.settings ?: return@onIntent
                dispatch(
                    state.copy(
                        settings = settingsState.copy(
                            diary = settingsState.diary.copy(
                                it.value
                            )
                        )
                    )
                )
                launch {
                    settingsProvider.provide(authScope, DiaryPagerEnabledSetting(it.value))
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}
