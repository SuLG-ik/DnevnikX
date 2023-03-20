package ru.sulgik.auth.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.sulgik.auth.mvi.AuthStore
import ru.sulgik.auth.ui.AuthConfirmScreen
import ru.sulgik.core.DIComponentContext
import ru.sulgik.modal.component.ModalComponentContext
import ru.sulgik.modal.component.ModalState

class AuthConfirmComponent(
    componentContext: DIComponentContext,
    initialUser: AuthStore.State.User? = null,
    private val onConfirm: () -> Unit,
    onHide: () -> Unit,
) : ModalComponentContext(componentContext, ModalState(initialUser != null), onHide = onHide) {

    private var user by mutableStateOf<AuthStore.State.User?>(initialUser)

    fun onUser(user: AuthStore.State.User) {
        this.user = user
        updateState(true)
    }

    private fun onCancel() {
        updateState(false)
    }

    private fun onConfirm() {
        onConfirm.invoke()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val user = user
        if (user != null) {
            AuthConfirmScreen(
                user = user,
                onConfirm = this::onConfirm,
                onCancel = this::onCancel,
                modifier = modifier,
            )
        }
    }
}