package ru.sulgik.diary.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.diary.mvi.DiaryStore
import ru.sulgik.ui.core.linkify
import ru.sulgik.ui.core.urlAt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryLessonInfoScreen(
    state: Pair<LocalDate, DiaryStore.State.Lesson>?,
    onFile: (DiaryStore.State.File) -> Unit,
    onLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var homeworkCopyIndex by rememberSaveable(state) { mutableStateOf(-1) }
    val timeFormatter = LocalTimeFormatter.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    if (state != null) {
        Column(
            modifier = modifier.padding(bottom = 10.dp)
        ) {
            TopAppBar(title = {
                Column {
                    Text(
                        text = state.second.title,
                        modifier = Modifier
                    )
                    Text(
                        text = "${timeFormatter.formatLiteral(state.first)}, ${
                            timeFormatter.format(
                                state.second.time
                            )
                        }",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
                actions = {
                    state.second.marks.forEach {
                        Text(
                            text = it.mark,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            color = it.value.markColor(),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                }
            )
            Column {
                if (state.second.homework.isEmpty()) {
                    ListItem(
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.diary_homework_color),
                                contentDescription = "добашняя работа",
                                modifier = Modifier.size(35.dp),
                            )
                        },
                        headlineContent = { Text("Нет заданий") },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                state.second.homework.forEachIndexed { index, homework ->
                    val homeworkText = homework.text.linkify()
                    ListItem(
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.diary_homework_color),
                                contentDescription = "добашняя работа",
                                modifier = Modifier.size(35.dp),
                            )
                        },
                        headlineContent = {
                            ClickableText(
                                text = homeworkText,
                                style = LocalTextStyle.current.copy(color = LocalContentColor.current),
                                onClick = {
                                    val url = homeworkText.urlAt(it)
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    if (url == null) {
                                        clipboardManager.setText(AnnotatedString(homework.text))
                                        homeworkCopyIndex = index
                                    } else {
                                        onLink(url)
                                    }
                                })
                        },
                        trailingContent = {
                            when (homeworkCopyIndex) {
                                index -> {
                                    Icon(
                                        painterResource(id = R.drawable.copy_done),
                                        contentDescription = "скопировано",
                                        modifier = Modifier.size(25.dp),
                                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                                    )
                                }

                                else -> {
                                    Icon(
                                        painterResource(id = R.drawable.copy),
                                        contentDescription = "скопировать",
                                        modifier = Modifier.size(25.dp),
                                        tint = LocalContentColor.current.copy(alpha = 0.6f),
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(homework.text))
                                    homeworkCopyIndex = index
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                            )
                    )
                }
                state.second.files.forEach { file ->
                    ListItem(
                        leadingContent = {
                            Image(
                                painterResource(id = R.drawable.diary_download_color),
                                contentDescription = "добашняя работа",
                                modifier = Modifier.size(35.dp),
                            )
                        },
                        headlineContent = { Text(file.name) },
                        trailingContent = {
                            Icon(
                                painterResource(id = R.drawable.diary_download),
                                contentDescription = "скачать",
                                modifier = Modifier.size(25.dp),
                                tint = LocalContentColor.current.copy(alpha = 0.6f),
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onFile(file) },
                            )
                    )
                }
            }
        }
    }
}

