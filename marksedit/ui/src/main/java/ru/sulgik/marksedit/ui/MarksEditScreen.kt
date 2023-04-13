package ru.sulgik.marksedit.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.marksedit.mvi.MarksEditStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.DesignedDivider
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.optionalBackNavigationIcon
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MarksEditScreen(
    lessonData: MarksEditStore.State.LessonData,
    changes: MarksEditStore.State.Changes,
    onAddMark: (Int) -> Unit,
    onClear: () -> Unit,
    onChangeStatus: (index: Int) -> Unit,
    isBackAvailable: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val isAutoScrolling = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContentWithPlaceholder(
                            isLoading = lessonData.isLoading,
                            state = lessonData.data,
                            placeholderContent = {
                                Text("Программирование", modifier = Modifier.defaultPlaceholder())
                            },
                        ) {
                            Text(it.title, modifier = Modifier.basicMarquee())
                        }
                        AnimatedContentWithPlaceholder(
                            isLoading = lessonData.isLoading,
                            state = lessonData.period,
                            placeholderContent = {
                                Text(
                                    "1 полугодие",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.defaultPlaceholder()
                                )
                            },
                        ) {
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                    }
                },
                actions = {
                    AnimatedContentWithPlaceholder(
                        isLoading = lessonData.isLoading,
                        state = lessonData.data,
                    ) {
                        if (it.averageValue != 0) {
                            Text(
                                text = it.average,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 20.sp,
                                color = it.averageValue.markColor(),
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    }

                },
                navigationIcon = optionalBackNavigationIcon(isBackAvailable, onBack)
            )
        },
        bottomBar = {
            MarksEditKeyboard(
                changes = changes,
                onAddMark = onAddMark,
                onClear = onClear,
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        floatingActionButton = {
            val isVisible =
                scrollState.maxValue != Int.MAX_VALUE && scrollState.maxValue != 0 && scrollState.maxValue != scrollState.value && !isAutoScrolling.value
            val alpha =
                animateFloatAsState(targetValue = if (isVisible) 1f else 0f, label = "fab_alpha")
            val offset =
                animateDpAsState(targetValue = if (isVisible) 0.dp else 40.dp, label = "fab_offset")
            FilledTonalIconButton(
                onClick = {
                    scope.launch {
                        scrollState.animateScrollTo(
                            scrollState.maxValue
                        )
                    }
                }, modifier = Modifier
                    .alpha(alpha = alpha.value)
                    .offset(y = offset.value)
            ) {
                Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "вниз")
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            AnimatedContentWithPlaceholder(
                isLoading = lessonData.isLoading,
                state = lessonData.data,
            ) { lesson ->
                val previousSize = remember { mutableStateOf(lesson.marks.size) }
                LaunchedEffect(key1 = lesson, block = {
                    if (previousSize.value < lesson.marks.size) {
                        isAutoScrolling.value = true
                        delay(25)
                        scrollState.animateScrollTo(
                            scrollState.maxValue
                        )
                        isAutoScrolling.value = false
                    }
                    previousSize.value = lesson.marks.size
                })
                LessonMarks(
                    lesson = lesson,
                    onChangeStatus = onChangeStatus,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(ExtendedTheme.dimensions.mainContentPadding)
                        .verticalScroll(scrollState)
                )
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mark(
    status: MarksEditStore.State.MarkStatus,
    mark: String,
    value: Int,
    date: LocalDate?,
    onChangeStatus: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha = animateFloatAsState(
        targetValue = if (status == MarksEditStore.State.MarkStatus.ENABLED) 1f else 0.4f,
        label = "mark_alpha",
    )
    Column(
        modifier = modifier
            .padding(top = 20.dp)
            .width(with(LocalDensity.current) {
                16.sp.toDp() * 3
            })
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onChangeStatus
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BadgedBox(badge = {
            Crossfade(targetState = status, label = "mark_status") {
                when (it) {
                    MarksEditStore.State.MarkStatus.DISABLED -> Icon(
                        painter = painterResource(id = R.drawable.plus),
                        contentDescription = "добавить",
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha.value),
                    )

                    MarksEditStore.State.MarkStatus.ENABLED -> Icon(
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "убрать",
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = alpha.value),
                    )
                }
            }

        }) {
            Text(
                text = mark,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 24.sp,
                color = value.markColor().copy(alpha = alpha.value)
            )
        }
        Text(
            text = date?.let { LocalTimeFormatter.current.format(it) } ?: "—",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 16.sp,
            color = LocalContentColor.current.copy(alpha = 0.5f * alpha.value),
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonMarks(
    lesson: MarksEditStore.State.Lesson,
    onChangeStatus: (index: Int) -> Unit,
    modifier: Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
    ) {
        lesson.marks.forEachIndexed { index, mark ->
            Mark(
                status = mark.status,
                mark = mark.mark,
                value = mark.value,
                date = mark.date,
                onChangeStatus = remember(
                    onChangeStatus,
                    index
                ) { { onChangeStatus(index) } },
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LessonMarksPlaceholder(
    modifier: Modifier
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterHorizontally),
    ) {
        repeat(12) {
            MarkPlaceholder()
        }
    }
}

val keyboardMarks = listOf(
    4, 5,
    2, 3
)

@Composable
private fun MarksEditStore.State.Changes.Change.buildText(): AnnotatedString {
    return buildAnnotatedString {
        withStyle(SpanStyle(color = value.markColor(), fontWeight = FontWeight.Bold)) {
            append(value.toString())
        }
        append(" на ${abs(offset)} ")
        if (offset > 0) {
            append("больше")
        } else {
            append("меньше")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MarksEditKeyboard(
    changes: MarksEditStore.State.Changes,
    onAddMark: (Int) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomEnd = CornerSize(0),
            bottomStart = CornerSize(0)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(ExtendedTheme.dimensions.mainContentPadding)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DesignedDivider(modifier = Modifier.fillMaxWidth())
            changes.changes.forEach {
                Text(it.buildText())
            }
            FlowRow(
                maxItemsInEachRow = 2,
                horizontalArrangement = Arrangement.spacedBy(12.5.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(7.5.dp, Alignment.CenterVertically),
                modifier = Modifier.fillMaxWidth(),
            ) {
                keyboardMarks.forEach {
                    MarksEditKeyboardButton(
                        value = it,
                        onAddMark = onAddMark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                    )
                }
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                ) {
                    Text("Очистить", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun MarksEditKeyboardButton(
    value: Int, onAddMark: (Int) -> Unit, modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = remember(value, onAddMark) { { onAddMark(value) } },
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = value.markColor(),
            containerColor = value.markColor().copy(alpha = 0.04f),
        ),
        modifier = modifier,
    ) {
        Text(value.toString(), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun Int.markColor(): Color {
    return when (this) {
        5 -> Color(0xFF4CAF50)
        4 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFF9800)
        2 -> Color(0xFFF44336)
        else -> LocalContentColor.current
    }
}
