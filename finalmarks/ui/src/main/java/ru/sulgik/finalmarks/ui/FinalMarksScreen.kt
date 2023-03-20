package ru.sulgik.finalmarks.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sulgik.finalmarks.mvi.FinalMarksStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.RefreshableBox
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.optionalBackNavigationIcon
import ru.sulgik.ui.core.outlined


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalMarksScreen(
    state: FinalMarksStore.State,
    backAvailable: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Итоговые оценки") },
                navigationIcon = optionalBackNavigationIcon(backAvailable, onBack),
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContentWithPlaceholder(
                isLoading = state.isLoading,
                state = state.lessons,
                placeholderContent = {
                    FinalMarksPlaceholder(modifier = Modifier.fillMaxSize())
                },
                content = {
                    FinalMarks(
                        state = it,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize()
                    )
                },
            )
        }
    }
}

@Composable
fun FinalMarksPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
    ) {
        repeat(12) {
            FinalMarksLessonPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ExtendedTheme.dimensions.contentSpaceBetween)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FinalMarks(
    state: FinalMarksStore.State.LessonsData,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalScroll = rememberScrollState()
    RefreshableBox(refreshing = state.isRefreshing, onRefresh = onRefresh) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
            modifier = Modifier
                .fillMaxSize(),
            content = {
                items(state.lessons.size, key = { it }, contentType = { "final_marks" }) {
                    val lesson = remember(state.lessons, it) { state.lessons[it] }
                    CompositionLocalProvider(
                        LocalOverscrollConfiguration provides null
                    ) {
                        FinalMarksLesson(
                            lesson = lesson,
                            scrollState = horizontalScroll,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        )
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinalMarksLesson(
    lesson: FinalMarksStore.State.Lesson,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp)
            .width(IntrinsicSize.Max),
    ) {
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
        ) {
            lesson.marks.forEach { mark ->
                MarkWithMessage(
                    mark = mark.mark,
                    value = mark.value,
                    subtitle = mark.period,
                    message = null,
                    onClick = {},
                )
            }
        }
    }
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


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FinalMarksLessonPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .outlined()
            .padding(15.dp)
            .width(IntrinsicSize.Max),
    ) {
        Text(
            text = "           Математика",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.defaultPlaceholder()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            repeat(6) {
                MarkPlaceholder()
            }
        }
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