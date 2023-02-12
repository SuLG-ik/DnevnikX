package ru.sulgik.dnevnikx.ui.auth

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.data.User
import ru.sulgik.dnevnikx.mvi.auth.AuthStore
import ru.sulgik.dnevnikx.mvi.getStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.repository.data.UserOutput
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.FloatingModalUI
import ru.sulgik.dnevnikx.ui.childDIContext

class AuthComponent(
    componentContext: DIComponentContext,
    private val onAuthenticated: (AuthScope) -> Unit,
) : BaseComponentContext(componentContext) {

    private val store = getStore<AuthStore>()

    private val confirmUser =
        AuthConfirmComponent(
            componentContext = childDIContext(key = "auth_confirm"),
            initialUser = store.state.authorizedUser?.toState(),
            onConfirm = this::onConfirmComplete,
        )


    private fun onConfirmComplete() {
        store.accept(AuthStore.Intent.ConfirmCompleted)
    }

    private val state by store.states(this) {
        if (it.authorizedUser != null && it.isConfirming) {
            confirmUser.onUser(it.authorizedUser.toState())
        }
        if (it.isCompleted && it.authorizedUser != null) {
            onAuthenticated(AuthScope(it.authorizedUser.id))
        }
        it
    }

    private fun onEditUsername(username: String) {
        store.accept(AuthStore.Intent.EditUsername(username))
    }

    private fun onEditPassword(password: String) {
        store.accept(AuthStore.Intent.EditPassword(password))
    }

    private fun onConfirm() {
        store.accept(AuthStore.Intent.Confirm)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        var isShown by rememberSaveable { mutableStateOf(true) }
        LaunchedEffect(key1 = null) {
            if(isShown) {
                isShown = false
            }
        }
        val focusRequester = LocalFocusManager.current
        LaunchedEffect(key1 = state.isLoading, block = {
            if (state.isLoading) {
                focusRequester.clearFocus()
            }
        })
        LaunchedEffect(key1 = confirmUser.modalState, block = {
            if (!confirmUser.modalState.isVisible) {
                store.accept(AuthStore.Intent.ResetAuth)
            }
        })
        FloatingModalUI(component = confirmUser, modifier = modifier) {
            AuthScreen(
                isLoading = state.isLoading || state.isConfirming || isShown ,
                username = state.username,
                password = state.password,
                error = state.error,
                onEditUsername = this::onEditUsername,
                onEditPassword = this::onEditPassword,
                onConfirm = this::onConfirm,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun UserOutput.toState(): User {
    return User(
        name = title,
    )
}
