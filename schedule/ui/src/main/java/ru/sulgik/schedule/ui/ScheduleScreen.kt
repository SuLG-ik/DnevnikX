package ru.sulgik.schedule.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.periods.ui.Period
import ru.sulgik.periods.ui.PeriodPlaceholders
import ru.sulgik.schedule.mvi.ScheduleStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.optionalBackNavigationIcon
import ru.sulgik.ui.core.outlined
import java.time.DayOfWeek.*
import java.time.Month.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    periods: ScheduleStore.State.Periods,
    schedule: ScheduleStore.State.Schedule,
    backAvailable: Boolean,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Расписание") },
                navigationIcon = optionalBackNavigationIcon(backAvailable, onBack),
            )
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
        val scrollState = rememberScrollState()
        LaunchedEffect(key1 = schedule.isLoading, block = {
            if (schedule.isLoading) {
                scrollState.animateScrollTo(0)
            }
        })
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            RefreshableBox(refreshing = schedule.isRefreshing, onRefresh = onRefresh) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            state = scrollState,
                            enabled = !schedule.isLoading,
                        ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        val scheduleData = schedule.schedule
                        when {
                            schedule.isLoading -> {
                                repeat(3) {
                                    ScheduleDatePlaceholder(
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth(),
                                    )
                                }
                            }

                            scheduleData != null -> {
                                scheduleData.schedule.forEach { diary ->
                                    ScheduleDate(
                                        diary = diary,
                                        modifier = Modifier
                                            .padding(horizontal = 10.dp)
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleDate(
    diary: ScheduleStore.State.ScheduleDate,
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
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            diary.lessonGroups.forEach {
                ScheduleLessonGroup(lessonGroup = it)
            }
        }
    }
}

@Composable
fun ScheduleDatePlaceholder(
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
                LessonsGroupPlaceholder()
            }
        }
    }
}

@Composable
fun ScheduleLessonGroup(
    lessonGroup: ScheduleStore.State.LessonGroup,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Text(
            text = lessonGroup.number,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            val lessons = lessonGroup.lessons
            lessons.forEachIndexed { index, lesson ->
                ScheduleLesson(
                    lesson = lesson,
                    showDate = index == 0 || lessons[index - 1].time != lesson.time,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun ScheduleLesson(
    lesson: ScheduleStore.State.Lesson,
    showDate: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        if (showDate)
            Text(
                text = LocalTimeFormatter.current.format(lesson.time),
                style = MaterialTheme.typography.bodyMedium
            )
        Text(
            text = buildAnnotatedString {
                append(lesson.title)
                if (lesson.group != null) {
                    withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                        append(" (${lesson.group})")
                    }
                }
            },
            style = MaterialTheme.typography.bodyLarge,
        )
        if (lesson.teacher.isNotBlank())
            Text(
                text = lesson.teacher,
                style = MaterialTheme.typography.bodyMedium
            )
    }
}


@Composable
fun LessonsGroupPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Text(
            text = "1",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.defaultPlaceholder()
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            LessonPlaceholder(
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun LessonPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "00:00 - 00:00",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.defaultPlaceholder(),
        )
        Text(
            text = "Математика",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.defaultPlaceholder(),
        )
        Text(
            text = "Иванов И. И.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.defaultPlaceholder(),
        )
    }
}


@Composable
fun PeriodSelector(
    periods: ScheduleStore.State.Periods,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContentWithPlaceholder(
        isLoading = periods.isLoading,
        state = periods.data,
        placeholderContent = {
            Row(
                modifier = modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PeriodPlaceholders()
            }
        },
        noDataContent = {
            NoData(modifier = Modifier.fillMaxWidth())
        },
        content = {
            Row(
                modifier = modifier
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(Modifier)
                CurrentPeriods(
                    period = it,
                    onSelect = onSelect,
                    onOther = onOther,
                )
                Spacer(Modifier)
            }
        }
    )
}

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
fun CurrentPeriods(
    period: ScheduleStore.State.PeriodsData,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
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
    Period(
        period = if (isOther) LocalTimeFormatter.current.format(selectedPeriod) else "Выбрать",
        selected = isOther,
        onSelect = onOther
    )
}
