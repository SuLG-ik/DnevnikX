package ru.sulgik.dnevnikx.ui.picker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import ru.sulgik.dnevnikx.ui.view.ListItemPicker
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Parcelable> PickerScreen(
    setupItem: PickerComponent.Info<T>,
    list: List<PickerComponent.Info<T>>,
    onContinue: (PickerComponent.Info<T>) -> Unit,
    modifier: Modifier = Modifier,
    marked: (PickerComponent.Info<T>) -> Boolean = { false },
) {
    var currentItem by rememberSaveable(setupItem, list) { mutableStateOf(setupItem) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (list.isNotEmpty())
            ListItemPicker(
                label = { it.title },
                marked = marked,
                value = currentItem,
                onValueChange = { currentItem = it },
                list = list,
                textStyle = MaterialTheme.typography.titleLarge,
            )
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedButton(onClick = { onContinue(currentItem) }) {
                Text("Выбрать")
            }
        }
    }
}