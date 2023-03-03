package ru.sulgik.dnevnikx.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.mvi.account.AccountStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext

class AccountComponent(
    componentContext: AuthorizedComponentContext,
    private val onSchedule: () -> Unit,
    private val onUpdates: () -> Unit,
    private val onFinalMarks: () -> Unit,
    private val onAbout: () -> Unit,
    private val onSelectAccount: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {


    val store: AccountStore = getStore()

    val state by store.states(this)

    @Composable
    override fun Content(modifier: Modifier) {
        AccountScreen(
            accountData = state.account,
            actionsData = state.actions,
            onSchedule = onSchedule,
            onUpdates = onUpdates,
            onAbout = onAbout,
            onFinalMarks = onFinalMarks,
            onSelectAccount = onSelectAccount,
            modifier = modifier,
        )
    }


}