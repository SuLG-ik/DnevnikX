package ru.sulgik.picker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.ui.core.ExtendedTheme

@Parcelize
data class PickerInfo<T : Parcelable>(
    val data: T,
    val title: String,
) : Parcelable

@Composable
fun <T : Parcelable> PickerScreen(
    setupItem: PickerInfo<T>,
    list: List<PickerInfo<T>>,
    modifier: Modifier = Modifier,
    autoContinuable: Boolean = true,
    onContinue: (PickerInfo<T>) -> Unit,
    marked: (PickerInfo<T>) -> Boolean = { false },
) {
    var currentItem by rememberSaveable(setupItem, list) { mutableStateOf(setupItem) }
    LaunchedEffect(key1 = Unit, block = {
        onContinue(currentItem)
    })
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (list.isNotEmpty())
            ListItemPicker(
                label = { it.title },
                marked = marked,
                value = currentItem,
                onValueChange = {
                    currentItem = it
                    if (autoContinuable) {
                        onContinue(it)
                    }
                },
                list = list,
                textStyle = MaterialTheme.typography.titleLarge,
            )
        if (!autoContinuable)
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                OutlinedButton(
                    onClick = { onContinue(currentItem) },
                    modifier = Modifier.padding(bottom = ExtendedTheme.dimensions.mainContentPadding)
                ) {
                    Text("Выбрать")
                }
            }
    }
}