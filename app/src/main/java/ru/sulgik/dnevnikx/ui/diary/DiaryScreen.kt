package ru.sulgik.dnevnikx.ui.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.mvi.diary.DiaryStore
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.ui.marks.markColor
import ru.sulgik.dnevnikx.ui.view.MiddleEllipsisText
import ru.sulgik.dnevnikx.ui.view.outlined
import ru.sulgik.dnevnikx.utils.defaultPlaceholder
import java.time.DayOfWeek.*
import java.time.Month.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    periods: DiaryStore.State.Periods,
    diary: DiaryStore.State.Diary,
    onSelect: (DatePeriod) -> Unit,
    onOther: () -> Unit,
    onLesson: (date: DiaryStore.State.DiaryDate, lesson: DiaryStore.State.Lesson) -> Unit,
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
        val scrollState = rememberScrollState()
        LaunchedEffect(key1 = diary.isLoading, block = {
            if (diary.isLoading) {
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
                        enabled = !diary.isLoading,
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    when {
                        diary.isLoading -> {
                            repeat(6) {
                                DiaryDatePlaceholder(
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .fillMaxWidth(),
                                )
                            }
                        }

                        diary.data != null -> {
                            diary.data.diary.forEach { diary ->
                                DiaryDate(
                                    diary = diary,
                                    onLesson = onLesson,
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
        if (diary.alert != null) {
            DiaryAlert(
                alert = diary.alert,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (diary.alert == null || !diary.alert.isOverload) {
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

@OptIn(ExperimentalMaterial3Api::class)
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


@OptIn(ExperimentalMaterial3Api::class)
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




@Composable
fun PeriodSelector(
    periods: DiaryStore.State.Periods,
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
    period: DiaryStore.State.PeriodsData,
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