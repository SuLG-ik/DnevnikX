package ru.sulgik.periods.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.LocalAnimatedContentTransition
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.outlined

@Composable
fun NoData(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Нет данных",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(5.dp),
        )
    }
}


@Composable
fun Period(
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
            .outlined(color = outlineColor.value)
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
        Text(
            text = period,
            style = MaterialTheme.typography.bodyMedium,
            color = color.value,
            modifier = Modifier,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
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
            .outlined(color = outlineColor.value)
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
                style = MaterialTheme.typography.bodyMedium,
                color = color.value,
                modifier = Modifier,
            )
        }
    }
}


@Composable
fun PeriodPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .outlined()
            .padding(7.dp)
    ) {
        Text(
            text = "Текущая неделя",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .defaultPlaceholder(true),
        )
    }
}

@Composable
fun PeriodPlaceholders(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
    ) {
        repeat(5) {
            PeriodPlaceholder()
        }
    }
}