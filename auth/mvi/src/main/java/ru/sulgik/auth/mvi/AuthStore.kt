package ru.sulgik.auth.mvi

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.sulgik.images.RemoteImage

interface AuthStore : Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> {

    sealed interface Intent {

        data class EditUsername(val value: String) : Intent
        data class EditPassword(val value: String) : Intent
        data class SelectVendor(val value: State.Vendor) : Intent
        object Confirm : Intent
        object ResetAuth : Intent
        object ConfirmCompleted : Intent

    }

    data class State(
        val isLoading: Boolean = false,
        val authField: AuthField = AuthField(),
        val vendorSelector: VendorSelector = VendorSelector(),
        val authorizedUser: User? = null,
        val isConfirming: Boolean = false,
        val isCompleted: Boolean = false,
        val isContinueAvailable: Boolean = false,
    ) {
        data class AuthField(
            val username: String = "",
            val password: String = "",
            val error: String? = null,
        )

        data class VendorSelector(
            val isLoading: Boolean = true,
            val vendors: ImmutableList<Vendor> = persistentListOf(),
            val selectedVendor: Vendor? = null,
        )


        data class Vendor(
            val region: String,
            val realName: String,
            val vendor: String,
            val host: String,
            val devKey: String,
            val logo: RemoteImage,
        )

        data class User(
            val title: String,
            val id: String,
            val token: String,
            val gender: Gender,
            val classes: List<Class>,
            val vendor: Vendor,
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