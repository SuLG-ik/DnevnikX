package ru.sulgik.dnevnikx.ui.profile.selector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.mvi.accountselector.AccountSelectorStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.AuthorizedModalComponentContext

class AccountSelectorComponent(
    componentContext: AuthorizedComponentContext,
    private val onAccountSelected: (Account) -> Unit,
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
        onAccountSelected.invoke(Account(account.id))
    }


    fun onAccountSelection() {
        updateState(true)
    }

}