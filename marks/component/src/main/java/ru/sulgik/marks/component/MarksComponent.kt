package ru.sulgik.marks.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.marks.mvi.MarksSettingsStore
import ru.sulgik.marks.mvi.MarksStore
import ru.sulgik.marks.ui.MarksScreen
import ru.sulgik.modal.ui.ModalUI

class MarksComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val store = getStore<MarksStore>()
    private val settingsStore = getStore<MarksSettingsStore>()

    private val markInfo = MarkInfoComponent(childDIContext("mark_info"), onHide = {
        store.accept(MarksStore.Intent.HideMark)
    })

    private val state by store.states(this) {
        markInfo.showMark(it.marks.selectedMark)
        it
    }

    private val settingsState by settingsStore.states(this)

    private fun onSelect(period: MarksStore.State.Period) {
        store.accept(MarksStore.Intent.SelectPeriod(period))
    }

    private fun onMark(mark: Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) {
        store.accept(MarksStore.Intent.SelectMark(mark))
    }

    private fun onRefresh(period: MarksStore.State.Period) {
        store.accept(MarksStore.Intent.RefreshMarks(period))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ModalUI(component = markInfo) {
            MarksScreen(
                periods = state.periods,
                marks = state.marks,
                settings = settingsState.settings,
                onSelect = this::onSelect,
                onMark = this::onMark,
                onRefresh = this::onRefresh,
                modifier = modifier,
            )
        }
    }

}