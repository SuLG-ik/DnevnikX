package ru.sulgik.account.selector.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface AccountSelectorStore: Store<AccountSelectorStore.Intent, AccountSelectorStore.State, AccountSelectorStore.Label> {

    sealed interface Intent

    data class State(
        val accounts: List<Account>? = null,
        val selectedAccount: Account? = null,
        val isLoading: Boolean = true,
    ) {

        data class Account(
            val id: String,
            val name: String,
            val selected: Boolean,
            val gender: Gender,
        )

        enum class Gender {
            MALE, FEMALE
        }

    }

    sealed interface Label

}