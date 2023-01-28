package ru.sulgik.dnevnikx.ui.diary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.mvi.diary.DiaryStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.repository.data.DatePeriod
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext

class DiaryComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    val store = getStore<DiaryStore>()

    val state by store.states(this)

    @Composable
    override fun Content(modifier: Modifier) {
        DiaryScreen(
            periods = state.periods,
            diary = state.diary,
            onSelect = this::onSelect,
            onOther = this::onOther,
            modifier = modifier
        )
    }

    private fun onSelect(period: DatePeriod) {
        store.accept(DiaryStore.Intent.SelectPeriod(period))
    }

    private fun onOther() {

    }

}