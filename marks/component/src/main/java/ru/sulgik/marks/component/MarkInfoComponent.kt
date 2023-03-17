package ru.sulgik.marks.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.DIComponentContext
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.marks.ui.MarkInfoScreen
import ru.sulgik.modal.component.ModalComponentContext

class MarkInfoComponent(
    componentContext: DIComponentContext,
    onHide: () -> Unit,
) : ModalComponentContext(componentContext, onHide = onHide) {

    private var currentMark by mutableStateOf<Pair<MarksStore.State.Lesson, MarksStore.State.Mark>?>(
        null
    )

    fun showMark(mark: Pair<MarksStore.State.Lesson, MarksStore.State.Mark>?) {
        if (mark == null) {
            updateState(false)
            return
        }
        currentMark = mark
        updateState(true)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val mark = currentMark
        if (mark != null) {
            MarkInfoScreen(mark = mark, modifier = modifier)
        }
    }
}