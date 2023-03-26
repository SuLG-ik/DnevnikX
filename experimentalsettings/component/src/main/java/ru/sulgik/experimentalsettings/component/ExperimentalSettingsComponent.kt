package ru.sulgik.experimentalsettings.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.persistentListOf
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.experimentalsettings.mvi.ExperimentalSettingsStore
import ru.sulgik.settings.ui.SettingsScreen
import ru.sulgik.settings.ui.SettingsScreenState

class ExperimentalSettingsComponent(
    componentContext: DIComponentContext,
    val backAvailable: Boolean = false,
    val onBack: () -> Unit = {},
) :
    BaseComponentContext(componentContext) {


    private val store: ExperimentalSettingsStore = getStore()

    private val state by store.states(this)

    private fun onBack() {
        if (backAvailable) {
            onBack.invoke()
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val settingsData = state.settings
        if (settingsData != null)
            SettingsScreen(
                state = SettingsScreenState(
                    title = "Скрытые настройки",
                    blocks = persistentListOf(),
                ),
                backAvailable = backAvailable,
                onBack = this::onBack,
                modifier = modifier,
            )
    }
}