package ru.sulgik.account.mvi

import com.arkivanov.mvikotlin.core.store.Store

interface AccountStore : Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> {

    sealed interface Intent

    data class State(
        val account: AccountData = AccountData(),
        val actions: ActionsData = ActionsData(),
    ) {
        data class ActionsData(
            val isLoading: Boolean = true,
            val actions: Actions? = null,
        )

        data class Actions(
            val isScheduleAvailable: Boolean,
            val isUpdatesAvailable: Boolean,
            val isFinalMarksAvailable: Boolean,
            val aboutData: AboutData,
        )

        data class AboutData(
            val applicationFullName: String,
        )

        data class AccountData(
            val isLoading: Boolean = true,
            val account: Account? = null,
        )

        data class Account(
            val name: String,
            val gender: Gender,
        )

        enum class Gender {
            MALE, FEMALE,
        }

    }

    sealed interface Label

}