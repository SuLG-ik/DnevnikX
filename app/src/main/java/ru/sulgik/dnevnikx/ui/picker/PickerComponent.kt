package ru.sulgik.dnevnikx.ui.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.ModalComponentContext
import java.time.LocalDate

class PickerComponent<T : Parcelable>(
    componentContext: DIComponentContext,
    private val onContinue: (Info<T>) -> Unit,
    private val marked: (Info<T>) -> Boolean = { false },
) : ModalComponentContext(componentContext) {

    private var dataState by mutableStateOf<Data<T>?>(null)

    fun setData(data: List<Info<T>>?, selectedData: Info<T>? = null) {
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


    private data class Data<T : Parcelable>(
        val setupData: Info<T>,
        val list: List<Info<T>>,
    )

    @Parcelize
    data class Info<T : Parcelable>(
        val data: T,
        val title: String,
    ) : Parcelable

}