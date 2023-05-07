package ru.sulgik.auth.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import ru.sulgik.auth.mvi.AuthStore
import ru.sulgik.auth.ui.VendorSelectorScreen
import ru.sulgik.core.DIComponentContext
import ru.sulgik.modal.component.ModalComponentContext

class AuthVendorSelectorComponent(
    componentContext: DIComponentContext,
    private val onVendorSelected: (AuthStore.State.Vendor) -> Unit,
    initialVendors: ImmutableList<AuthStore.State.Vendor>? = null,
    onHide: () -> Unit = {},
) : ModalComponentContext(componentContext, onHide = onHide) {

    private var vendors by mutableStateOf(initialVendors)

    fun onVendor() {
        updateState(true)
    }


    fun updateVendors(vendors: ImmutableList<AuthStore.State.Vendor>) {
        if (vendors != this.vendors)
            this.vendors = vendors
    }


    private fun onVendorSelected(vendor: AuthStore.State.Vendor) {
        onVendorSelected.invoke(vendor)
        onCancel()
    }


    private fun onCancel() {
        updateState(false)
    }


    @Composable
    override fun Content(modifier: Modifier) {
        val vendors = vendors
        if (vendors?.isNotEmpty() == true) {
            VendorSelectorScreen(
                vendors = vendors,
                onSelectVendor = this::onVendorSelected,
                modifier = modifier,
            )
        }
    }
}