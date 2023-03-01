package ru.sulgik.dnevnikx.ui.marks

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.ui.diary.NoData
import ru.sulgik.dnevnikx.ui.diary.Period
import ru.sulgik.dnevnikx.ui.diary.PeriodPlaceholder
import ru.sulgik.dnevnikx.ui.view.outlined
import ru.sulgik.dnevnikx.utils.defaultPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarksScreen(
    periods: MarksStore.State.Periods,
    marks: MarksStore.State.Marks,
    onSelect: (MarksStore.State.Period) -> Unit,
    onMark: (Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Отметки") })
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
            val scrollState = rememberScrollState()
            LaunchedEffect(key1 = marks.isLoading, block = {
                if (marks.isLoading) {
                    scrollState.animateScrollTo(0)
                }
            })
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state = scrollState,
                        enabled = !marks.isLoading,
                    ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                when {
                    marks.isLoading -> {
                        repeat(6) {
                            LessonPlaceholder(
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(),
                            )
                        }
                    }

                    marks.data != null -> {
                        marks.data.lessons.forEach { lesson ->
                            Lesson(
                                lesson = lesson,
                                onMark = { mark -> onMark(lesson to mark) },
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Lesson(
    lesson: MarksStore.State.Lesson,
    onMark: (MarksStore.State.Mark) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            )
            if (lesson.averageValue != 0)
                Text(
                    text = lesson.average,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = lesson.averageValue.markColor()
                )
        }
        Spacer(modifier = Modifier.height(15.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            lesson.marks.forEach { mark ->
                MarkWithMessage(mark = mark, onClick = onMark)
            }
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
            text = LocalTimeFormatter.current.format(date),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 16.sp,
            color = LocalContentColor.current.copy(alpha = 0.5f)
        )
    }
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
                periods.data.periods.forEach {
                    Period(
                        period = it.title,
                        selected = it == periods.data.selectedPeriod,
                        onSelect = { onSelect(it) },
                    )
                }
            }
        }
        Spacer(Modifier)

    }
}
