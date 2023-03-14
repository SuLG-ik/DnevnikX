package ru.sulgik.diary.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import org.koin.core.component.get
import ru.sulgik.common.platform.UriHandler
import ru.sulgik.core.DIComponentContext
import ru.sulgik.diary.mvi.DiaryStore
import ru.sulgik.diary.ui.DiaryLessonInfoScreen
import ru.sulgik.modal.component.ModalComponentContext

class DiaryLessonInfoComponent(
    componentContext: DIComponentContext,
    onHide: () -> Unit,
) : ModalComponentContext(componentContext, onHide = onHide) {


    private val uriHandler = get<UriHandler>()

    private var currentLesson by mutableStateOf<Pair<LocalDate, DiaryStore.State.Lesson>?>(
        null
    )

    fun showLesson(date: LocalDate, lesson: DiaryStore.State.Lesson) {
        currentLesson = date to lesson
        updateState(true)
    }

    private fun onFile(file: DiaryStore.State.File) {
        uriHandler.open(file.url)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        DiaryLessonInfoScreen(
            state = currentLesson,
            onFile = this::onFile,
            modifier = modifier,
        )
    }
}