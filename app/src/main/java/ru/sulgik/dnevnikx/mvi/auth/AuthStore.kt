package ru.sulgik.dnevnikx.mvi.auth

import com.arkivanov.mvikotlin.core.store.Store
import ru.sulgik.dnevnikx.repository.data.UserOutput

interface AuthStore : Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> {

    sealed interface Intent {

        data class EditUsername(val value: String) : Intent
        data class EditPassword(val value: String) : Intent
        object Confirm : Intent
        object ResetAuth : Intent
        object ConfirmCompleted : Intent

    }

    data class State(
        val isLoading: Boolean = false,
        val username: String = "",
        val password: String = "",
        val error: String? = null,
        val authorizedUser: UserOutput? = null,
        val isConfirming: Boolean = false,
        val isCompleted: Boolean = false,
    )

    sealed interface Label

}