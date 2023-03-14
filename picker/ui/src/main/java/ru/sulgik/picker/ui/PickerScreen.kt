package ru.sulgik.picker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize

@Parcelize
data class PickerInfo<T : Parcelable>(
    val data: T,
    val title: String,
) : Parcelable

@Composable
fun <T : Parcelable> PickerScreen(
    setupItem: PickerInfo<T>,
    list: List<PickerInfo<T>>,
    onContinue: (PickerInfo<T>) -> Unit,
    modifier: Modifier = Modifier,
    marked: (PickerInfo<T>) -> Boolean = { false },
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