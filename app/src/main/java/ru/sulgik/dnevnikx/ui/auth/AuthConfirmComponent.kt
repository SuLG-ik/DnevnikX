package ru.sulgik.dnevnikx.ui.auth

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.data.User
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.ModalComponentContext
import ru.sulgik.dnevnikx.ui.ModalState

class AuthConfirmComponent(
    componentContext: DIComponentContext,
    initialUser: User? = null,
    private val onConfirm: () -> Unit,
) : ModalComponentContext(componentContext, ModalState(initialUser != null)) {

    private var user by mutableStateOf<User?>(initialUser)

    fun onUser(user: User) {
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
                modifier = modifier.padding(15.dp),
            )
        }
    }
}