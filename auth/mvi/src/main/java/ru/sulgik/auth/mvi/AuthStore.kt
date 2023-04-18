package ru.sulgik.auth.mvi

import com.arkivanov.mvikotlin.core.store.Store

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
        val authorizedUser: User? = null,
        val isConfirming: Boolean = false,
        val isCompleted: Boolean = false,
    ) {
        data class User(
            val title: String,
            val id: String,
            val token: String,
            val gender: Gender,
            val classes: List<Class>
        ) {
            enum class Gender {
                MALE, FEMALE,
            }
        }

        data class Class(
            val fullTitle: String,
        )

    }

    sealed interface Label

}