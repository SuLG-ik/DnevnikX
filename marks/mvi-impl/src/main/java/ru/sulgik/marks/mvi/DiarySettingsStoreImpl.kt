package ru.sulgik.marks.mvi

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
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.diary.settings.DiaryPagerEnabledSetting
import ru.sulgik.settings.provider.SettingsProvider
import ru.sulgik.settings.provider.getSettingFlow

@OptIn(ExperimentalMviKotlinApi::class)
class MarksSettingsStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    settingsProvider: SettingsProvider,
    authScope: AuthScope,
) : MarksSettingsStore,
    Store<MarksSettingsStore.Intent, MarksSettingsStore.State, MarksSettingsStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "MarksSettingsStoreImpl",
        initialState = MarksSettingsStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                settingsProvider.getSettingFlow<DiaryPagerEnabledSetting>(authScope)
                    .onEach {
                        dispatch(state.copy(MarksSettingsStore.State.MarksSettings(it.enabled)))
                    }
                    .flowOn(Dispatchers.Main)
                    .launchIn(this)
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}