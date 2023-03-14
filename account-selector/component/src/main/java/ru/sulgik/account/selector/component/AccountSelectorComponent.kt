package ru.sulgik.account.selector.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.account.selector.mvi.AccountSelectorStore
import ru.sulgik.account.selector.ui.AccountSelectorScreen
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.modal.component.AuthorizedModalComponentContext

class AccountSelectorComponent(
    componentContext: AuthorizedComponentContext,
    private val onAccountSelected: (AuthScope) -> Unit,
    private val onAddAccount: () -> Unit,
) : AuthorizedModalComponentContext(componentContext) {

    private val store: AccountSelectorStore = getStore()

    private val state by store.states(this)

    @Composable
    override fun Content(modifier: Modifier) {
        val accounts = state.accounts
        if (accounts != null) {
            AccountSelectorScreen(
                accounts = accounts,
                onAccountSelected = this::onAccountSelected,
                onAddAccount = onAddAccount,
                modifier = modifier,
            )
        }
    }

    private fun onAccountSelected(account: AccountSelectorStore.State.Account) {
        onAccountSelected.invoke(AuthScope(account.id))
    }


    fun onAccountSelection() {
        updateState(true)
    }

}