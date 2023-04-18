package ru.sulgik.schedule.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.schedule.mvi.ScheduleListStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleListScreen(
    schedule: ScheduleListStore.State.Schedule,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContentWithPlaceholder(
        schedule.data.isLoading,
        state = schedule.data,
        label = "schedule_placeholder",
        placeholderContent = {
            ScheduleDatePlaceholders(
                modifier = Modifier.fillMaxSize(),
            )
        },
        content = {
            ScheduleDates(
                data = it,
                onRefresh = { onRefresh() },
                modifier = Modifier.fillMaxSize(),
            )
        }
    )
}

@Composable
fun ScheduleDates(
    data: ScheduleListStore.State.ScheduleData,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RefreshableBox(refreshing = data.isRefreshing, onRefresh = onRefresh) {
        Column(
            modifier = modifier
                .verticalScroll(
                    state = rememberScrollState(),
                    enabled = !data.isLoading,
                ),
            verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
        ) {
            data.schedule.forEach { schedule ->
                ScheduleDate(
                    diary = schedule,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}


@Composable
fun ScheduleDate(
    diary: ScheduleListStore.State.ScheduleDate,
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
fun ScheduleDatePlaceholders(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
    ) {
        repeat(3) {
            ScheduleDatePlaceholder(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun ScheduleLessonGroup(
    lessonGroup: ScheduleListStore.State.LessonGroup,
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
    lesson: ScheduleListStore.State.Lesson,
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