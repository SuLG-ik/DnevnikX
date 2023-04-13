package ru.sulgik.marksedit.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.getParameterizedStore
import ru.sulgik.core.states
import ru.sulgik.marksedit.mvi.MarksEditStore
import ru.sulgik.marksedit.ui.MarksEditScreen

class MarksEditComponent(
    period: DatePeriod,
    periodTitle: String,
    title: String,
    componentContext: AuthorizedComponentContext,
    private val isBackAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    private val store: MarksEditStore =
        getParameterizedStore(
            MarksEditStore.Params(
                period = MarksEditStore.Params.Period(
                    periodTitle,
                    period
                ), title = title
            )
        )

    private val state by store.states(this)

    private fun onAddMark(value: Int) {
        store.accept(MarksEditStore.Intent.AddMark(value))
    }

    private fun onChangeStatus(index: Int) {
        store.accept(MarksEditStore.Intent.ChangeStatus(index))
    }

    private fun onClear() {
        store.accept(MarksEditStore.Intent.Clear)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        MarksEditScreen(
            lessonData = state.lessonData,
            onAddMark = this::onAddMark,
            onChangeStatus = this::onChangeStatus,
            onClear = this::onClear,
            isBackAvailable = isBackAvailable,
            onBack = onBack,
            modifier = modifier
        )
    }

}