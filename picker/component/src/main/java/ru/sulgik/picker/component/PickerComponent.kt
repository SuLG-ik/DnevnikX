package ru.sulgik.picker.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.picker.ui.PickerInfo
import ru.sulgik.picker.ui.PickerScreen

class PickerComponent<T : Parcelable>(
    componentContext: DIComponentContext,
    private val onContinue: (PickerInfo<T>) -> Unit,
    private val marked: (PickerInfo<T>) -> Boolean = { false },
    private val autoContinuable: Boolean = true,
    setupData: Data<T>? = null,
) : BaseComponentContext(componentContext) {

    data class Data<T : Parcelable>(
        val setupData: PickerInfo<T>,
        val list: List<PickerInfo<T>>,
    )

    var data by mutableStateOf<Data<T>?>(setupData)
        private set

    fun setData(data: List<PickerInfo<T>>?, selectedData: PickerInfo<T>? = null) {
        if (data == null) {
            this.data = null
            return
        }
        this.data = Data(
            setupData = selectedData ?: data.last(),
            list = data,
        )
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val state = data
        if (state != null) {
            PickerScreen(
                setupItem = state.setupData,
                list = state.list,
                onContinue = onContinue,
                autoContinuable = autoContinuable,
                marked = marked,
                modifier = modifier,
            )
        }
    }


}