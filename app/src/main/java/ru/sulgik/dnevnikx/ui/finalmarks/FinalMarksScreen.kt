package ru.sulgik.dnevnikx.ui.finalmarks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.mvi.finalmarks.FinalMarksStore
import ru.sulgik.dnevnikx.ui.marks.MarkPlaceholder
import ru.sulgik.dnevnikx.ui.marks.MarkWithMessage
import ru.sulgik.dnevnikx.ui.view.RefreshableBox
import ru.sulgik.dnevnikx.ui.view.optionalBackNavigationIcon
import ru.sulgik.dnevnikx.ui.view.outlined
import ru.sulgik.dnevnikx.utils.defaultPlaceholder


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
            val verticalScroll = rememberScrollState()
            val horizontalScroll = rememberScrollState()
            RefreshableBox(refreshing = state.isRefreshing, onRefresh = onRefresh) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            state = verticalScroll,
                            enabled = !state.isLoading,
                        ),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    when {
                        state.lessons != null -> {
                            state.lessons.lessons.forEach {
                                CompositionLocalProvider(
                                    LocalOverscrollConfiguration provides null
                                ) {
                                    FinalMarksLesson(
                                        lesson = it,
                                        scrollState = horizontalScroll,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp)
                                    )
                                }
                            }
                        }

                        else -> {
                            repeat(12) {
                                FinalMarksLessonPlaceholder(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp)
                                )
                            }
                        }
                    }

                }
            }
        }
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