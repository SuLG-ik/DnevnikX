package ru.sulgik.marks.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.ui.core.ExtendedTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkInfoScreen(
    mark: Pair<MarksStore.State.Lesson, MarksStore.State.Mark>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = ExtendedTheme.dimensions.mainContentPadding)
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