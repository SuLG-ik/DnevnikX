package ru.sulgik.settings.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableCollection
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.optionalBackNavigationIcon
import ru.sulgik.ui.core.outlined

data class SettingsScreenState(
    val title: String,
    val blocks: ImmutableCollection<SettingsScreenBlock>,
)

data class SettingsScreenBlock(
    val title: String,
    val items: ImmutableCollection<SettingsScreenItem>,
)

sealed interface SettingsScreenItem {


    val title: AnnotatedString
    val overlineTitle: AnnotatedString?
    val supportingTitle: AnnotatedString?
    val icon: Painter?

    data class Switch(
        override val title: AnnotatedString,
        val currentState: Boolean,
        val onToggle: (Boolean) -> Unit,
        override val icon: Painter? = null,
        override val overlineTitle: AnnotatedString? = null,
        override val supportingTitle: AnnotatedString? = null,
    ) : SettingsScreenItem

    data class Button(
        override val title: AnnotatedString,
        val onClick: () -> Unit,
        override val icon: Painter? = null,
        override val overlineTitle: AnnotatedString? = null,
        override val supportingTitle: AnnotatedString? = null,
    ) : SettingsScreenItem

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsScreenState,
    backAvailable: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val savedState by rememberUpdatedState(newValue = state)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(state.title) },
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
                savedState.blocks.forEach { block ->
                    block.Content()
                }
            }
        }
    }
}


@Composable
private fun SettingsScreenBlock.Content(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            color = LocalContentColor.current.copy(alpha = 0.65f)
        )
        Spacer(modifier = Modifier)
        items.forEach { item ->
            item.Content(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SettingsScreenItem.Content(modifier: Modifier = Modifier) {
    when (this) {
        is SettingsScreenItem.Button -> SettingsItemButton(this, modifier)
        is SettingsScreenItem.Switch -> SettingsItemToggle(this, modifier)
    }
}

@Composable
private fun SettingsItemToggle(item: SettingsScreenItem.Switch, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(item.title)
        },
        supportingContent = item.supportingTitle?.let {
            {
                Text(it)
            }
        },
        overlineContent = item.overlineTitle?.let {
            {
                Text(it)
            }
        },
        leadingContent = item.icon?.let {
            {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(ExtendedTheme.dimensions.leadingIconSizeLarge)
                )
            }
        },
        trailingContent = {
            Switch(checked = item.currentState, onCheckedChange = item.onToggle)
        },
        modifier = modifier,
    )
}

@Composable
private fun SettingsItemButton(item: SettingsScreenItem.Button, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = {
            Text(item.title)
        },
        supportingContent = item.supportingTitle?.let {
            {
                Text(it)
            }
        },
        overlineContent = item.overlineTitle?.let {
            {
                Text(it)
            }
        },
        leadingContent = item.icon?.let {
            {
                Image(
                    painter = it,
                    contentDescription = null,
                    modifier = Modifier.size(ExtendedTheme.dimensions.leadingIconSizeLarge)
                )
            }
        },
        modifier = modifier
            .outlined()
            .clickable(onClick = item.onClick)
    )
}