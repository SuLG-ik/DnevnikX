package ru.sulgik.dnevnikx.ui.marks

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.marks.MarksStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.ModalUI
import ru.sulgik.dnevnikx.ui.childDIContext

class MarksComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val store = getStore<MarksStore>()

    private val markInfo = MarkInfoComponent(childDIContext("mark_info"))

    private val state by store.states(this) {
        if (it.marks.data?.selectedMark != null) {
            markInfo.showMark(it.marks.data.selectedMark)
        }
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
        ModalUI(component = markInfo, modifier) {
            MarksScreen(
                periods = state.periods,
                marks = state.marks,
                onSelect = this::onSelect,
                onMark = this::onMark,
                onRefresh = this::onRefresh,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

}