package ru.sulgik.dnevnikx.ui.finalmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.mvi.finalmarks.FinalMarksStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext

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