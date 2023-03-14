package ru.sulgik.picker.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import ru.sulgik.core.DIComponentContext
import ru.sulgik.modal.component.ModalComponentContext
import ru.sulgik.picker.ui.PickerInfo
import ru.sulgik.picker.ui.PickerScreen

class PickerComponent<T : Parcelable>(
    componentContext: DIComponentContext,
    private val onContinue: (PickerInfo<T>) -> Unit,
    private val marked: (PickerInfo<T>) -> Boolean = { false },
    onHide: () -> Unit = { },
) : ModalComponentContext(componentContext, onHide = onHide) {

    private data class Data<T : Parcelable>(
        val setupData: PickerInfo<T>,
        val list: List<PickerInfo<T>>,
    )


    private var dataState by mutableStateOf<Data<T>?>(null)


    fun setData(data: List<PickerInfo<T>>?, selectedData: PickerInfo<T>? = null) {
        if (data == null) {
            updateState(false)
            return
        }
        dataState = Data(
            setupData = selectedData ?: data.last(),
            list = data,
        )
        updateState(true)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val state = dataState
        Column {
            if (state != null) {
                PickerScreen(
                    setupItem = state.setupData,
                    list = state.list,
                    onContinue = onContinue,
                    modifier = modifier,
                    marked = marked,
                )
            }
        }
    }


}