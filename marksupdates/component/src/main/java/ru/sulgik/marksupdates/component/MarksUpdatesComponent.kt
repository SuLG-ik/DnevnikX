package ru.sulgik.marksupdates.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.marksupdates.mvi.MarksUpdatesStore
import ru.sulgik.marksupdates.ui.MarksUpdateScreen

class MarksUpdatesComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean,
    private val onBack: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {

    private val store: MarksUpdatesStore = getStore()

    private fun onRefresh() {
        store.accept(MarksUpdatesStore.Intent.Refresh)
    }

    private fun onLoadNextPage() {
        store.accept(MarksUpdatesStore.Intent.LoadNextPage)
    }

    private val state by store.states(this)

    @Composable
    override fun Content(modifier: Modifier) {
        MarksUpdateScreen(
            updates = state.updates,
            backAvailable = backAvailable,
            onBack = onBack,
            onRefresh = this::onRefresh,
            onLoadNextPage = this::onLoadNextPage
        )
    }

}