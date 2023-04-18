package ru.sulgik.picker.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.modal.component.ModalComponentContext
import ru.sulgik.picker.ui.PickerInfo

class ModalPickerComponent<T : Parcelable>(
    componentContext: DIComponentContext,
    onContinue: (PickerInfo<T>) -> Unit,
    marked: (PickerInfo<T>) -> Boolean = { false },
    onHide: () -> Unit = { },
    setupData: PickerComponent.Data<T>? = null,
) : ModalComponentContext(componentContext, onHide = onHide) {

    private val innerPicker = PickerComponent<T>(
        componentContext = childDIContext("inner_picker"),
        onContinue = onContinue,
        marked = marked,
        autoContinuable = false,
        setupData = setupData,
    )

    fun setData(data: List<PickerInfo<T>>?, selectedData: PickerInfo<T>? = null) {
        if (data == null) {
            updateState(false)
            return
        }
        innerPicker.setData(data, selectedData)
        updateState(true)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        innerPicker.Content(modifier = modifier)
    }


}