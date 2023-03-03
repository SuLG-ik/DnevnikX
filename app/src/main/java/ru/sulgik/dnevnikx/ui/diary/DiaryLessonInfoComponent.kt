package ru.sulgik.dnevnikx.ui.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import org.koin.core.component.get
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.mvi.diary.DiaryStore
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.platform.UriHandler
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.ModalComponentContext
import ru.sulgik.dnevnikx.ui.marks.markColor

class DiaryLessonInfoComponent(
    componentContext: DIComponentContext,
    onHide: () -> Unit,
) : ModalComponentContext(componentContext, onHide = onHide) {


    private val uriHandler = get<UriHandler>()
    private var homeworkCopyIndex = mutableStateOf(-1)

    private var currentLesson by mutableStateOf<Pair<LocalDate, DiaryStore.State.Lesson>?>(
        null
    )

    fun showLesson(date: LocalDate, lesson: DiaryStore.State.Lesson) {
        currentLesson = date to lesson
        updateState(true)
    }

    override fun updateState(isVisible: Boolean) {
        super.updateState(isVisible)
        if (!isVisible) {
            homeworkCopyIndex.value = -1
        }
    }

    private fun onFile(file: DiaryStore.State.File) {
        uriHandler.open(file.url)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val timeFormatter = LocalTimeFormatter.current
        val clipboardManager = LocalClipboardManager.current
        val state = currentLesson
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
                            headlineText = { Text("Нет заданий") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    state.second.homework.forEachIndexed { index, homework ->
                        ListItem(
                            leadingContent = {
                                Image(
                                    painterResource(id = R.drawable.diary_homework_color),
                                    contentDescription = "добашняя работа",
                                    modifier = Modifier.size(35.dp),
                                )
                            },
                            headlineText = { Text(homework.text) },
                            trailingContent = {
                                when (homeworkCopyIndex.value) {
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
                                        homeworkCopyIndex.value = index
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
                            headlineText = { Text(file.name) },
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
}