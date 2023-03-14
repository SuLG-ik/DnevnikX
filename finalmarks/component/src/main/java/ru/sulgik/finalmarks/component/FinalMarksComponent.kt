package ru.sulgik.finalmarks.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.finalmarks.mvi.FinalMarksStore
import ru.sulgik.finalmarks.ui.FinalMarksScreen

class FinalMarksComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    val store: FinalMarksStore = getStore()

    val state by store.states(this)

    private fun onRefresh() {
        store.accept(FinalMarksStore.Intent.RefreshFinalMarks)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        FinalMarksScreen(
            state = state,
            backAvailable = backAvailable,
            onBack = onBack,
            onRefresh = this::onRefresh,
            modifier = modifier,
        )
    }

}