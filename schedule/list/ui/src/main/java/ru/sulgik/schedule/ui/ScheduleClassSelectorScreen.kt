package ru.sulgik.schedule.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.sulgik.schedule.mvi.ScheduleListHostStore
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.LocalAnimatedContentTransition
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScheduleClassSelectorScreen(
    selectedClass: ScheduleListHostStore.State.ClassData,
    classes: ImmutableList<ScheduleListHostStore.State.ClassData>,
    onSelect: (ScheduleListHostStore.State.ClassData) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            ExtendedTheme.dimensions.contentSpaceBetween,
            Alignment.CenterHorizontally
        ),
        verticalArrangement = Arrangement.spacedBy(
            ExtendedTheme.dimensions.contentSpaceBetween,
            Alignment.CenterVertically
        ),
        modifier = modifier.padding(ExtendedTheme.dimensions.mainContentPadding)
    ) {
        classes.forEach {
            ClassItem(
                data = it,
                onClick = { onSelect(it) },
                isSelected = it == selectedClass,
            )
        }
        AddAnotherButton(
            onClick = onAdd,
        )
    }
}

@Composable
private fun ClassItem(
    data: ScheduleListHostStore.State.ClassData,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedPeriod(
        data.fullTitle,
        isSelected,
        onClick,
        modifier,
    )
}

@Composable
private fun AddAnotherButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        Icons.Outlined.Add,
        contentDescription = "добавить",
        modifier = modifier
            .outlined()
            .clickable(onClick = onClick)
            .padding(7.dp)
            .size(21.dp),
    )
}

@Composable
fun AnimatedPeriod(
    period: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color =
        animateColorAsState(
            targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            label = "PeriodColorAnimation ($period)"
        )
    val outlineColor =
        animateColorAsState(
            targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            label = "PeriodOutlinedColorAnimation ($period)"
        )
    Row(
        modifier = modifier
            .outlined(color = outlineColor.value, shape = MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onSelect)
            .padding(7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        AnimatedVisibility(visible = selected) {
            Row {
                Icon(
                    Icons.Outlined.Done,
                    contentDescription = "выбран",
                    modifier = Modifier.size(15.dp),
                    tint = color.value,
                )
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        val transition = LocalAnimatedContentTransition.current
        AnimatedContent(
            targetState = period,
            transitionSpec = { transition },
            label = "period_text",
        ) {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = color.value,
                modifier = Modifier,
            )
        }
    }
}
