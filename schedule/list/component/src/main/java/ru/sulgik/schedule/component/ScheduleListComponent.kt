package ru.sulgik.schedule.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.schedule.mvi.ScheduleListStore
import ru.sulgik.schedule.ui.ScheduleListScreen

class ScheduleListComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val store = getStore<ScheduleListStore>()

    private val state by store.states(this)

    private fun onRefresh() {
        store.accept(ScheduleListStore.Intent.RefreshSchedule)
    }

    fun onSelectClass(classFullTitle: String) {
        store.accept(ScheduleListStore.Intent.SelectClass(classFullTitle))
    }

    @Composable
    override fun Content(modifier: Modifier) {
        ScheduleListScreen(
            schedule = state.schedule,
            onRefresh = this::onRefresh,
            modifier = modifier,
        )
    }

}