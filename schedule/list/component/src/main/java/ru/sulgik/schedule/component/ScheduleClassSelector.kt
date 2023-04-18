package ru.sulgik.schedule.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import ru.sulgik.core.DIComponentContext
import ru.sulgik.modal.component.ModalComponentContext
import ru.sulgik.schedule.mvi.ScheduleListHostStore
import ru.sulgik.schedule.ui.ScheduleClassSelectorScreen

class ScheduleClassSelector(
    componentContext: DIComponentContext,
    setupData: Data?,
    private val onSelect: (selectedClass: ScheduleListHostStore.State.ClassData) -> Unit,
    private val onAdd: () -> Unit,
) : ModalComponentContext(
    componentContext
) {
    data class Data(
        val selectedClass: ScheduleListHostStore.State.ClassData,
        val classes: ImmutableList<ScheduleListHostStore.State.ClassData>,
    )

    var data by mutableStateOf<Data?>(setupData)
        private set

    fun setData(
        classes: ImmutableList<ScheduleListHostStore.State.ClassData>?,
        selectedClass: ScheduleListHostStore.State.ClassData?
    ) {
        if (classes == null || selectedClass == null) {
            this.data = null
            return
        }
        this.data = Data(
            selectedClass = selectedClass,
            classes = classes
        )
    }

    private fun onSelect(selectedClass: ScheduleListHostStore.State.ClassData) {
        updateState(false)
        onSelect.invoke(selectedClass)
    }

    private fun onAdd() {
        onAdd.invoke()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val data = data
        if (data != null) {
            ScheduleClassSelectorScreen(
                selectedClass = data.selectedClass,
                classes = data.classes,
                onAdd = this::onAdd,
                onSelect = this::onSelect,
                modifier = modifier,
            )
        }
    }
}