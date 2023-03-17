package ru.sulgik.experimentalsettings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sulgik.experimentalsettings.mvi.ExperimentalSettingsStore
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.optionalBackNavigationIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentalSettingsScreen(
    settingsData: ExperimentalSettingsStore.State.SettingsData,
    onToggleNestedScreenTransition: (Boolean) -> Unit,
    backAvailable: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Скрытые настройки") },
                navigationIcon = optionalBackNavigationIcon(backAvailable, onBack)
            )
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(ExtendedTheme.dimensions.mainContentPadding),
                verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
            ) {
                SwitchSettings(
                    overlineTitle = "Может вызвать проблемы с размером",
                    title = "Анимация переходов",
                    supportingTitle = "Переходы между вложенными экранами",
                    currentState = settingsData.ui.isNestedScreenTransitionEnabled,
                    onToggle = onToggleNestedScreenTransition,
                )
            }
        }
    }
}

@Composable
fun SwitchSettings(
    currentState: Boolean,
    onToggle: (Boolean) -> Unit,
    title: String,
    supportingTitle: String,
    overlineTitle: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(title)
        },
        supportingContent =
        {
            Text(supportingTitle)
        },
        overlineContent =
        {
            Text(overlineTitle)
        },
        trailingContent = {
            Switch(checked = currentState, onCheckedChange = onToggle)
        },
        modifier = modifier,
    )
}
