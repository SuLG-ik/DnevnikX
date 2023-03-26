package ru.sulgik.diary.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.diary.mvi.DiaryStore
import ru.sulgik.periods.ui.AnimatedPeriod
import ru.sulgik.periods.ui.NoData
import ru.sulgik.periods.ui.Period
import ru.sulgik.periods.ui.PeriodPlaceholders
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.MiddleEllipsisText
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.flingBehaviour
import ru.sulgik.ui.core.outlined
import java.time.DayOfWeek.*
import java.time.Month.*

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun DiaryScreen(
    periods: DiaryStore.State.Periods,
    diary: DiaryStore.State.Diary,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    onLesson: (date: DiaryStore.State.DiaryDate, lesson: DiaryStore.State.Lesson) -> Unit,
    onRefresh: (DatePeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Дневник") })
        },
        modifier = modifier,
        bottomBar = {
            PeriodSelector(
                periods = periods,
                onSelect = onSelect,
                onOther = onOther,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            val periodsData = periods.data
            if (periodsData == null) {
                DiaryDatePlaceholders(
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
                    val period =
                        remember(periodsData.periods, pageIndex) { periodsData.periods[pageIndex] }
                    val diaryData = remember(diary.data, period) { diary.data[period] }
                    AnimatedContentWithPlaceholder(
                        diaryData?.isLoading ?: true,
                        state = diaryData,
                        label = "diary_placeholder",
                        placeholderContent = {
                            DiaryDatePlaceholders()
                        },
                        content = {
                            DiaryDateData(
                                diary = it,
                                onLesson = onLesson,
                                onRefresh = { onRefresh(period) },
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    )
                }
            }
        }
    }
}

val diaryInListModifier = Modifier
    .fillMaxSize()

val diaryDateInListModifier = Modifier
    .padding(horizontal = 10.dp)
    .fillMaxWidth()

@Composable
fun DiaryDateData(
    diary: DiaryStore.State.DiaryData,
    onLesson: (date: DiaryStore.State.DiaryDate, lesson: DiaryStore.State.Lesson) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RefreshableBox(
        refreshing = diary.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = diaryInListModifier,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                items(diary.diary.size, key = { it }, contentType = { "diary" }) {
                    val diaryDate = remember(diary.diary, it) {
                        diary.diary[it]
                    }
                    DiaryDate(
                        diary = diaryDate,
                        onLesson = onLesson,
                        modifier = diaryDateInListModifier,
                    )
                }
            }
        )
    }
}

@Composable
fun DiaryDate(
    diary: DiaryStore.State.DiaryDate,
    onLesson: (date: DiaryStore.State.DiaryDate, lesson: DiaryStore.State.Lesson) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp)
    ) {
        Text(
            text = LocalTimeFormatter.current.formatLiteral(diary.date),
            style = MaterialTheme.typography.labelLarge,
        )
        Spacer(modifier = Modifier.height(15.dp))
        val diaryAlert = diary.alert
        if (diaryAlert != null) {
            DiaryAlert(
                alert = diaryAlert,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (diaryAlert == null || !diaryAlert.isOverload) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                diary.lessons.forEach {
                    DiaryLesson(lesson = it, onLesson = { lesson -> onLesson(diary, lesson) })
                }
            }
        }
    }
}

@Composable
fun DiaryAlert(alert: DiaryStore.State.DiaryAlert, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
            Icon(
                painter = painterResource(id = R.drawable.diary_alert),
                contentDescription = "сообщение",
                modifier = Modifier.size(25.dp)
            )
            Text(
                text = alert.message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }

    }
}


@Composable
fun DiaryDatePlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp)
    ) {
        Text(
            text = "Понедельник, 1 января",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.defaultPlaceholder()
        )
        Spacer(modifier = Modifier.height(15.dp))
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(6) {
                DiaryLessonPlaceholder()
            }
        }
    }
}

@Composable
fun DiaryDatePlaceholders(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(4) {
            DiaryDatePlaceholder(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
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

@Composable
fun DiaryLesson(
    lesson: DiaryStore.State.Lesson,
    onLesson: (lesson: DiaryStore.State.Lesson) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onLesson(lesson) }
        ),
    ) {
        Text(
            text = lesson.number,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            Text(
                text = LocalTimeFormatter.current.format(lesson.time),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.bodyLarge,
            )
            lesson.homework.forEach {
                Homework(it.text)
            }
            lesson.files.forEach {
                File(
                    text = it.name,
                )
            }
        }
        Marks(
            lesson.marks,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Marks(marks: List<DiaryStore.State.Mark>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        marks.forEach {
            Mark(it)
        }
    }
}

@Composable
fun Mark(mark: DiaryStore.State.Mark, modifier: Modifier = Modifier) {
    Text(
        text = mark.mark,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = mark.value.markColor()
    )
}

@Composable
fun Homework(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = R.drawable.diary_homework),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun File(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.diary_download),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        MiddleEllipsisText(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun DiaryLessonPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Text(
            text = "1",
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.7f),
            modifier = Modifier.defaultPlaceholder()
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            Text(
                text = "1.     Математика",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.defaultPlaceholder()
            )
        }
        Text(
            text = "5",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            modifier = Modifier.defaultPlaceholder()
        )
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PeriodSelector(
    periods: DiaryStore.State.Periods,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
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
            label = "period_placeholder",
            modifier = Modifier.fillMaxWidth(),
            placeholderContent = {
                PeriodPlaceholders()
            }, noDataContent = {
                NoData(modifier = Modifier.fillMaxWidth())
            }, content = {
                CurrentPeriods(
                    scrollState = scrollState,
                    period = it,
                    onSelect = onSelect,
                    onOther = onOther,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        )
        Spacer(Modifier.width(10.dp))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentPeriods(
    scrollState: ScrollState,
    period: DiaryStore.State.PeriodsData,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val previousPeriod = period.previousPeriod
        val selectedPeriod = period.selectedPeriod
        if (previousPeriod != null) {
            Period(
                period = "Предыдущая",
                selected = previousPeriod == selectedPeriod,
                onSelect = { onSelect(previousPeriod) }
            )
        }
        val currentPeriod = period.currentPeriod
        if (currentPeriod != null) {
            Period(
                period = "Текущая неделя",
                selected = currentPeriod == selectedPeriod,
                onSelect = { onSelect(currentPeriod) },
            )
        }
        val nextPeriod = period.nextPeriod
        if (nextPeriod != null) {
            Period(
                period = "Следующая",
                selected = nextPeriod == selectedPeriod,
                onSelect = { onSelect(nextPeriod) }
            )
        }
        val isOther =
            currentPeriod != selectedPeriod && nextPeriod != selectedPeriod && previousPeriod != selectedPeriod && currentPeriod != null
        val currentOther =
            if (isOther) LocalTimeFormatter.current.format(selectedPeriod) else "Выбрать"
        LaunchedEffect(key1 = isOther, block = {
            if (isOther) {
                scrollState.animateScrollTo(Int.MAX_VALUE)
            }
        })
        AnimatedPeriod(
            period = currentOther,
            selected = isOther,
            onSelect = onOther
        )
    }
}
