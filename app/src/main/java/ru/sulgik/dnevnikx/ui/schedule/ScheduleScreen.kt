package ru.sulgik.dnevnikx.ui.schedule

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.mvi.schedule.ScheduleStore
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.ui.view.outlined
import ru.sulgik.dnevnikx.utils.defaultPlaceholder
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
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Расписание") },
                navigationIcon = if (backAvailable) {
                    {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "назад")
                        }
                    }
                } else {
                    {}
                }
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = scrollState,
                        enabled = !schedule.isLoading,
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = {
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

                        schedule.schedule != null -> {
                            schedule.schedule.schedule.forEach { diary ->
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
            text = diary.date.format(),
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

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
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


private fun LocalDate.format(): String {
    return "${formatWeek()}, $dayOfMonth ${formatMonth()}"
}

private fun LocalDate.formatMonth(): String {
    return when (month) {
        JANUARY -> "января"
        FEBRUARY -> "февраля"
        MARCH -> "марта"
        APRIL -> "апреля"
        MAY -> "мая"
        JUNE -> "июня"
        JULY -> "июля"
        AUGUST -> "августа"
        SEPTEMBER -> "сентября"
        OCTOBER -> "октября"
        NOVEMBER -> "ноября"
        DECEMBER -> "декабря"
    }
}

private fun LocalDate.formatWeek(): String {
    return when (dayOfWeek) {
        MONDAY -> "Понедельник"
        TUESDAY -> "Вторник"
        WEDNESDAY -> "Среда"
        THURSDAY -> "Четверг"
        FRIDAY -> "Пятница"
        SATURDAY -> "Суббота"
        SUNDAY -> "Воскресенье"
    }
}

@Composable
fun PeriodSelector(
    periods: ScheduleStore.State.Periods,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .animateContentSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Spacer(Modifier)
        when {
            periods.isLoading -> {
                repeat(5) {
                    PeriodPlaceholder()
                }
            }

            periods.data == null || periods.data.periods.isEmpty() -> {
                NoData(modifier = Modifier.fillMaxWidth())
            }

            else -> {
                CurrentPeriods(
                    period = periods.data,
                    onSelect = onSelect,
                    onOther = onOther,
                )
            }
        }
        Spacer(Modifier)

    }
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
    if (period.previousPeriod != null) {
        Period(
            period = "Предыдущая",
            selected = period.previousPeriod == period.selectedPeriod,
            onSelect = { onSelect(period.previousPeriod) }
        )
    }
    if (period.currentPeriod != null) {
        Period(
            period = "Текущая неделя",
            selected = period.currentPeriod == period.selectedPeriod,
            onSelect = { onSelect(period.currentPeriod) },
        )
    }
    if (period.nextPeriod != null) {
        Period(
            period = "Следующая",
            selected = period.nextPeriod == period.selectedPeriod,
            onSelect = { onSelect(period.nextPeriod) }
        )
    }
    val isOther =
        period.currentPeriod != period.selectedPeriod && period.nextPeriod != period.selectedPeriod && period.previousPeriod != period.selectedPeriod && period.currentPeriod != null
    Period(
        period = if (isOther) LocalTimeFormatter.current.format(period.selectedPeriod) else "Выбрать",
        selected = isOther,
        onSelect = onOther
    )
}

@Composable
fun Period(
    period: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color =
        animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
    val outlineColor =
        animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
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