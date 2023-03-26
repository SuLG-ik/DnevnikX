package ru.sulgik.marksupdates.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.marksupdates.mvi.MarksUpdatesStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.optionalBackNavigationIcon
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksUpdateScreen(
    updates: MarksUpdatesStore.State.MarksUpdates,
    backAvailable: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Обновления оценок") },
                navigationIcon = optionalBackNavigationIcon(backAvailable, onBack),
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            val lazyState = rememberScrollState()
            LaunchedEffect(key1 = lazyState.maxValue, key2 = lazyState.value, block = {
                if (lazyState.maxValue - lazyState.value <= 1500) {
                    onLoadNextPage()
                }
            })
            AnimatedContentWithPlaceholder(
                isLoading = updates.isLoading,
                state = updates.data,
                placeholderContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween)
                    ) {
                        MarksUpdatePlaceholder(
                            count = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ExtendedTheme.dimensions.mainContentPadding),
                        )
                        MarksUpdatePlaceholder(
                            count = 15,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ExtendedTheme.dimensions.mainContentPadding),
                        )
                    }
                }) {
                RefreshableBox(
                    refreshing = it.isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(lazyState),
                        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween)
                    ) {
                        MarksUpdate(
                            title = "Недавние",
                            state = it.latest,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ExtendedTheme.dimensions.mainContentPadding),
                        )
                        MarksUpdate(
                            title = "Устаревшие",
                            state = it.old,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = ExtendedTheme.dimensions.mainContentPadding),
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MarksUpdate(
    title: String,
    state: MarksUpdatesStore.State.MarksUpdatesPeriodData,
    modifier: Modifier = Modifier,
) {
    if (state.data.isNotEmpty())
        Column(
            modifier = modifier
                .outlined()
                .padding(15.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                state.data.forEach { update ->
                    MarkUpdate(update, Modifier.fillMaxWidth())
                }
                if (state.isNextPageLoading) {
                    repeat(3) {
                        MarkUpdatePlaceholder(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
}

@Composable
fun MarksUpdatePlaceholder(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp),
    ) {
        Text(
            text = "Устаревшие",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .defaultPlaceholder()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(count) {
                MarkUpdatePlaceholder(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun MarkUpdate(update: MarksUpdatesStore.State.MarkUpdate, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            Text(
                text = LocalTimeFormatter.current.formatLiteral(update.lesson.date),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = update.lesson.name,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Mark(data = update)
    }
}

@Composable
fun MarkUpdatePlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            Text(
                text = "Суббота, 25 числа",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.defaultPlaceholder(),
            )
            Text(
                text = "Самый важный предмет",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.defaultPlaceholder(),
            )
        }
        Text(text = "5", modifier = Modifier.defaultPlaceholder())
    }
}

val arrowModifier = Modifier.size(24.dp)

@Composable
fun Mark(data: MarksUpdatesStore.State.MarkUpdate, modifier: Modifier = Modifier) {
    val previousMark = data.previous
    if (previousMark == null) {
        Text(
            text = data.current.mark,
            color = data.current.value.markColor(),
            modifier = modifier
        )
    } else {
        Row(
            modifier = modifier,
        ) {
            Text(
                text = previousMark.mark,
                color = previousMark.value.markColor(),
            )
            Icon(
                painterResource(id = R.drawable.mark_arrow),
                contentDescription = "изменена на",
                modifier = arrowModifier,
            )
            Text(
                text = data.current.mark,
                color = data.current.value.markColor(),
            )
        }
    }
}

@Composable
fun Int.markColor(): Color {
    return when (this) {
        5 -> Color(0xFF4CAF50)
        4 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFF9800)
        2 -> Color(0xFFF44336)
        else -> LocalContentColor.current.copy(alpha = 0.6f)
    }
}
