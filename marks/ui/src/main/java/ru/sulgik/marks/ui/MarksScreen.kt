package ru.sulgik.marks.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.marks.mvi.MarksStore
import ru.sulgik.periods.ui.NoData
import ru.sulgik.periods.ui.Period
import ru.sulgik.periods.ui.PeriodPlaceholders
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.flingBehaviour
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarksScreen(
    periods: MarksStore.State.Periods,
    marks: MarksStore.State.Marks,
    onSelect: (MarksStore.State.Period) -> Unit,
    onMark: (Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) -> Unit,
    onRefresh: (MarksStore.State.Period) -> Unit,
    onEdit: (MarksStore.State.Period, MarksStore.State.Lesson) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Оценки") })
        },
        bottomBar = {
            PeriodSelector(
                periods = periods,
                onSelect = onSelect,
                modifier = Modifier.padding(top = 10.dp)
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val periodsData = periods.data
            if (periodsData == null) {
                MarksPlaceholders(
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val pagerState =
                    rememberPagerState(periodsData.periods.indexOf(periodsData.selectedPeriod))
                LaunchedEffect(key1 = periodsData.selectedPeriod, block = {
                    if (periodsData.periods[pagerState.targetPage] != periodsData.selectedPeriod) {
                        pagerState.scrollToPage(periodsData.periods.indexOf(periodsData.selectedPeriod))
                    }
                })
                LaunchedEffect(
                    key1 = pagerState,
                ) {
                    snapshotFlow { pagerState.currentPage }.collect {
                        onSelect(periodsData.periods[it])
                    }
                }
                HorizontalPager(
                    pageCount = periodsData.periods.size,
                    key = { it },
                    flingBehavior = pagerState.flingBehaviour(),
                    state = pagerState,
                ) { pageIndex ->
                    val period = periodsData.periods[pageIndex]
                    val marksData = marks.data[period]
                    AnimatedContentWithPlaceholder(
                        isLoading = marksData?.isLoading ?: true, state = marksData,
                        placeholderContent = {
                            MarksPlaceholders()
                        },
                        content = { lessons ->
                            MarksDate(
                                marksLesson = lessons,
                                onMark = onMark,
                                onEdit = { onEdit(period, it) },
                                onRefresh = { onRefresh(period) })
                        },
                    )
                }
            }
        }

    }
}

private val lessonInListModifier = Modifier
    .padding(horizontal = 10.dp)
    .fillMaxWidth()

@Composable
fun MarksDate(
    marksLesson: MarksStore.State.MarksLesson,
    onMark: (Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) -> Unit,
    onRefresh: () -> Unit,
    onEdit: (MarksStore.State.Lesson) -> Unit,
    modifier: Modifier = Modifier,
) {
    RefreshableBox(
        refreshing = marksLesson.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
        ) {
            items(marksLesson.lessons.size, key = { it }, contentType = { "marks" }) {
                val lesson = remember(marksLesson.lessons, it) { marksLesson.lessons[it] }
                Lesson(
                    lesson = lesson,
                    onMark = remember(lesson, onMark) { { mark -> onMark(lesson to mark) } },
                    onEdit = remember(lesson, onEdit) { { onEdit(lesson) } },
                    modifier = lessonInListModifier
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Lesson(
    lesson: MarksStore.State.Lesson,
    onMark: (MarksStore.State.Mark) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEdit
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    painterResource(id = R.drawable.edit),
                    contentDescription = "edit",
                    modifier = Modifier
                        .size(15.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }


            if (lesson.averageValue != 0)
                Text(
                    text = lesson.average,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = lesson.averageValue.markColor()
                )
        }
        Spacer(modifier = Modifier.height(10.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            lesson.marks.forEach { mark ->
                MarkWithMessage(mark = mark, onClick = onMark)
            }
        }
    }
}

@Composable
fun MarksPlaceholders(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
    ) {
        repeat(6) {
            LessonPlaceholder(modifier = Modifier.padding(horizontal = ExtendedTheme.dimensions.mainContentPadding))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Урок математики",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .defaultPlaceholder()
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )
            Text(
                text = "5",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                modifier = Modifier
                    .defaultPlaceholder(),
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(13.dp),
        ) {
            repeat(12) {
                MarkPlaceholder()
            }
        }
    }
}


@Composable
private fun MarkWithMessage(
    mark: MarksStore.State.Mark,
    onClick: (MarksStore.State.Mark) -> Unit,
    modifier: Modifier = Modifier,
) {
    MarkWithMessage(
        mark = mark.mark,
        value = mark.value,
        date = mark.date,
        message = mark.message,
        onClick = { onClick(mark) },
        modifier = modifier,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkWithMessage(
    mark: String,
    value: Int,
    date: LocalDate,
    message: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MarkWithMessage(
        mark = mark,
        value = value,
        subtitle = LocalTimeFormatter.current.format(date),
        message = message,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun MarkPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(5.dp)
            .size(width = 40.dp, height = 60.dp)
            .defaultPlaceholder(),
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkWithMessage(
    mark: String,
    value: Int,
    subtitle: String,
    message: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = message != null, onClick = { onClick() })
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BadgedBox(badge = {
            if (message != null) {
                Badge {
                    Icon(
                        painter = painterResource(id = R.drawable.mark_message_badge),
                        contentDescription = "сообщение",
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }) {
            Text(
                text = mark,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = value.markColor()
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 16.sp,
            color = LocalContentColor.current.copy(alpha = 0.5f),
        )
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


@Composable
fun PeriodSelector(
    periods: MarksStore.State.Periods,
    onSelect: (MarksStore.State.Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .horizontalScroll(scrollState, !periods.isLoading),
    ) {
        Spacer(Modifier.width(10.dp))
        AnimatedContentWithPlaceholder(
            isLoading = periods.isLoading,
            state = periods.data,
            placeholderContent = {
                PeriodPlaceholders()
            },
            noDataContent = {
                NoData()
            },
            content = { periodsData ->
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    periodsData.periods.forEach { period ->
                        Period(
                            period = period.title,
                            selected = period == periodsData.selectedPeriod,
                            onSelect = { onSelect(period) },
                        )
                    }
                }
            })
        Spacer(Modifier.width(10.dp))
    }
}
