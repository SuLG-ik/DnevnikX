package ru.sulgik.schedule.add.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.sulgik.schedule.add.mvi.ScheduleClassesEditStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.DesignedDivider
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.optionalBackNavigationIcon
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditScreen(
    classes: ScheduleClassesEditStore.State.SavedClasses,
    selector: ScheduleClassesEditStore.State.ClassSelector,
    onAddClass: () -> Unit,
    onDeleteClass: (number: String, group: String) -> Unit,
    backAvailable: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    classNumberSelectorContent: @Composable () -> Unit,
    classGroupSelectorContent: @Composable () -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Расписание классов") },
            navigationIcon = optionalBackNavigationIcon(backAvailable, onBack),
        )
    }, modifier = modifier, bottomBar = {
        Surface(
            shadowElevation = 4.dp, shape = MaterialTheme.shapes.extraLarge.copy(
                bottomStart = CornerSize(0), bottomEnd = CornerSize(0)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ExtendedTheme.dimensions.mainContentPadding),
            ) {
                DesignedDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f, fill = false),
                        contentAlignment = Alignment.Center,
                    ) {
                        classNumberSelectorContent()
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f, fill = false),
                        contentAlignment = Alignment.Center,
                    ) {
                        classGroupSelectorContent()
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    OutlinedButton(onClick = onAddClass, enabled = selector.isAvailable) {
                        Text("Добавить")
                    }
                }
            }

        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ExtendedTheme.dimensions.mainContentPadding),
        ) {
            AnimatedContentWithPlaceholder(isLoading = classes.isLoading, state = classes.data) {
                ClassesList(
                    classes = it.classes,
                    onDelete = { onDeleteClass(it.number, it.group) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ClassesList(
    classes: List<ScheduleClassesEditStore.State.ClassData>,
    onDelete: (ScheduleClassesEditStore.State.ClassData) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        classes.forEach {
            Class(data = it, onDelete = { onDelete(it) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Class(
    data: ScheduleClassesEditStore.State.ClassData,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDelete,
        )
    ) {
        Text(
            text = "${data.fullTitle} класс",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .padding(5.dp)
                .outlined(shape = MaterialTheme.shapes.extraLarge)
                .align(Alignment.Center)
                .padding(10.dp)
        )
        if (!data.isPermanent)
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(start = 5.dp, bottom = 5.dp)
            ) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "удалить",
                    modifier = Modifier.size(15.dp)
                )
            }
        else
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(start = 5.dp, bottom = 5.dp)
            )
    }
}