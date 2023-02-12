package ru.sulgik.dnevnikx.ui.marks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.ModalComponentContext

class MarkInfoComponent(
    componentContext: DIComponentContext,
) : ModalComponentContext(componentContext) {

    private var currentMark by mutableStateOf<Pair<MarksStore.State.Lesson, MarksStore.State.Mark>?>(
        null
    )

    fun showMark(mark: Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) {
        currentMark = mark
        updateState(true)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val mark = currentMark
        if (mark != null) {
            Column(
                modifier = modifier.padding(bottom = 10.dp)
            ) {
                TopAppBar(title = {
                    Text(
                        text = "${mark.first.title}, ${LocalTimeFormatter.current.format(mark.second.date)}",
                        modifier = Modifier
                    )
                },
                    actions = {
                        Text(
                            text = mark.second.mark,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 20.sp,
                            color = mark.second.value.markColor(),
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }
                )
                Text(
                    mark.second.message ?: "нет сообщения",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}