package ru.sulgik.schedule.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.childAuthContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.modal.ui.FloatingModalUI
import ru.sulgik.schedule.mvi.ScheduleListHostStore
import ru.sulgik.schedule.ui.ScheduleListHostScreen
import ru.sulgik.ui.modal.ModalSheetDirection

class ScheduleListHostComponent(
    componentContext: AuthorizedComponentContext,
    onSelectClass: () -> Unit,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    private val classList = ScheduleListComponent(
        componentContext = childAuthContext("schedule_list"),
    )

    private val store = getStore<ScheduleListHostStore>()

    private val classSelector = ScheduleClassSelector(
        childDIContext("class_selector"),
        store.state.savedClasses.data?.let {
            ScheduleClassSelector.Data(
                selectedClass = it.selectedClass,
                classes = it.classes,
            )
        },
        onSelect = this::onClassSelect,
        onAdd = onSelectClass,
    )

    private val state by store.states(this) {
        val savedClassesData = it.savedClasses.data ?: return@states it
        classSelector.setData(savedClassesData.classes, savedClassesData.selectedClass)
        onClassSelect(savedClassesData.selectedClass)
        it
    }

    private fun onClassSelect(selectedClass: ScheduleListHostStore.State.ClassData) {
        classList.onSelectClass(selectedClass.fullTitle)
        store.accept(ScheduleListHostStore.Intent.SelectClass(selectedClass))
    }

    private fun onBack() {
        if (backAvailable) {
            onBack.invoke()
        }
    }

    private fun onSelectClass() {
        classSelector.updateState(true)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ScheduleListHostScreen(
            savedClasses = state.savedClasses,
            onSelectClass = this::onSelectClass,
            backAvailable = backAvailable,
            onBack = this::onBack,
            modifier = modifier,
        ) {
            FloatingModalUI(component = classSelector, direction = ModalSheetDirection.TOP) {
                classList.Content(modifier = Modifier)
            }
        }
    }

}