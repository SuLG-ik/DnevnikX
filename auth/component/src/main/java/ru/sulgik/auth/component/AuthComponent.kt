package ru.sulgik.auth.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.mvi.AuthStore
import ru.sulgik.auth.ui.AuthScreen
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.childDIContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states
import ru.sulgik.modal.ui.FloatingModalUI

class AuthComponent(
    componentContext: DIComponentContext,
    private val onAuthenticated: (AuthScope) -> Unit,
    private val isBackAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseComponentContext(componentContext) {

    private val store = getStore<AuthStore>()

    private val vendorSelector = AuthVendorSelectorComponent(
        componentContext = childDIContext(key = "vendor_selector"),
        { store.accept(AuthStore.Intent.SelectVendor(it)) },
        initialVendors = store.state.vendorSelector.vendors,
    )

    private val confirmUser =
        AuthConfirmComponent(
            componentContext = childDIContext(key = "auth_confirm"),
            initialUser = store.state.authorizedUser,
            onConfirm = this::onConfirmComplete,
            onHide = this::onAuthCancel,
        )


    private fun onAuthCancel() {
        store.accept(AuthStore.Intent.ResetAuth)
    }

    private fun onConfirmComplete() {
        store.accept(AuthStore.Intent.ConfirmCompleted)
    }

    private fun onBack() {
        onBack.invoke()
    }

    private val state by store.states(this) {
        val user = it.authorizedUser
        if (user != null && it.isConfirming) {
            confirmUser.onUser(user)
        }
        if (it.isCompleted && user != null) {
            onAuthenticated(AuthScope(user.id))
        }
        if (!it.vendorSelector.isLoading) {
            vendorSelector.updateVendors(it.vendorSelector.vendors)
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


    private fun onSelectVendor() {
        vendorSelector.onVendor()
    }


    @Composable
    override fun Content(modifier: Modifier) {
        val focusRequester = LocalFocusManager.current
        LaunchedEffect(key1 = state.isLoading, block = {
            if (state.isLoading) {
                focusRequester.clearFocus()
            }
        })
        FloatingModalUI(component = confirmUser) {
            FloatingModalUI(component = vendorSelector) {
                AuthScreen(
                    state = state,
                    isVendorSelecting = vendorSelector.modalState.isVisible,
                    onEditUsername = this::onEditUsername,
                    onEditPassword = this::onEditPassword,
                    onSelectVendor = this::onSelectVendor,
                    onConfirm = this::onConfirm,
                    isBackAvailable = isBackAvailable,
                    onBack = this::onBack,
                    modifier = modifier
                )
            }
        }
    }
}
