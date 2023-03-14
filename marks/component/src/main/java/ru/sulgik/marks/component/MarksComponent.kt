package ru.sulgik.marks.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.marks.ui.MarksScreen
import ru.sulgik.modal.ui.ModalUI

class MarksComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val store = getStore<MarksStore>()

    private val markInfo = MarkInfoComponent(childDIContext("mark_info"), onHide = {
        store.accept(MarksStore.Intent.HideMark)
    })

    private val state by store.states(this) {
        markInfo.showMark(it.marks.data?.selectedMark)
        it
    }

    private fun onSelect(period: MarksStore.State.Period) {
        store.accept(MarksStore.Intent.SelectPeriod(period))
    }

    private fun onMark(mark: Pair<MarksStore.State.Lesson, MarksStore.State.Mark>) {
        store.accept(MarksStore.Intent.SelectMark(mark))
    }

    private fun onRefresh() {
        store.accept(MarksStore.Intent.RefreshMarks)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ModalUI(component = markInfo) {
            MarksScreen(
                periods = state.periods,
                marks = state.marks,
                onSelect = this::onSelect,
                onMark = this::onMark,
                onRefresh = this::onRefresh,
                modifier = modifier,
            )
        }
    }

}