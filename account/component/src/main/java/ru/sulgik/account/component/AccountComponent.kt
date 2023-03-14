package ru.sulgik.account.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.account.mvi.AccountStore
import ru.sulgik.account.ui.AccountScreen
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states

class AccountComponent(
    componentContext: AuthorizedComponentContext,
    private val onSchedule: () -> Unit,
    private val onUpdates: () -> Unit,
    private val onFinalMarks: () -> Unit,
    private val onAbout: () -> Unit,
    private val onSelectAccount: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {


    private val store: AccountStore = getStore()

    private val state by store.states(this)

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