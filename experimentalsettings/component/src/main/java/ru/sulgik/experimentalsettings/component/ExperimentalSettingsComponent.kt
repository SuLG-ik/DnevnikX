package ru.sulgik.experimentalsettings.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import kotlinx.collections.immutable.persistentListOf
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.experimentalsettings.mvi.ExperimentalSettingsStore
import ru.sulgik.settings.ui.SettingsScreen
import ru.sulgik.settings.ui.SettingsScreenBlock
import ru.sulgik.settings.ui.SettingsScreenItem
import ru.sulgik.settings.ui.SettingsScreenState

class ExperimentalSettingsComponent(
    componentContext: DIComponentContext,
    val isBackAvailable: Boolean = false,
    val onBack: () -> Unit = {},
) :
    BaseComponentContext(componentContext) {


    private val store: ExperimentalSettingsStore = getStore()

    private val state by store.states(this)

    private fun onBack() {
        if (isBackAvailable) {
            onBack.invoke()
        }
    }

    private fun onToggleNestedScreenTransition(value: Boolean) {
        store.accept(ExperimentalSettingsStore.Intent.ToggleNestedScreenTransition(value))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val settingsData = state.settings
        if (settingsData != null)
            SettingsScreen(
                state = SettingsScreenState(
                    title = "Скрытые настройки",
                    blocks = persistentListOf(
                        SettingsScreenBlock(
                            title = "Интерфейс",
                            items = persistentListOf(
                                SettingsScreenItem.Switch(
                                    overlineTitle = AnnotatedString("Может вызвать проблемы с размером"),
                                    title = AnnotatedString("Анимация переходов"),
                                    supportingTitle = AnnotatedString("Переходы между вложенными экранами"),
                                    currentState = settingsData.ui.isNestedScreenTransitionEnabled,
                                    onToggle = this::onToggleNestedScreenTransition,
                                )
                            )
                        )
                    ),
                ),
                backAvailable = isBackAvailable,
                onBack = this::onBack,
                modifier = modifier,
            )
    }

}