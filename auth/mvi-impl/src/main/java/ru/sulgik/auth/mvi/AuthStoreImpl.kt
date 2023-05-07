package ru.sulgik.auth.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.LocalAccountDataRepository
import ru.sulgik.account.domain.LocalAccountRepository
import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.account.domain.data.AccountId
import ru.sulgik.account.domain.data.Gender
import ru.sulgik.auth.domain.MergedAuthRepository
import ru.sulgik.auth.domain.MergedVendorAuthRepository
import ru.sulgik.auth.domain.RemoteAuthRepository
import ru.sulgik.auth.domain.data.Authorization
import ru.sulgik.auth.domain.data.UserOutput
import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.common.ErrorResponseException
import ru.sulgik.core.syncDispatch
import ru.sulgik.kacher.core.on

@OptIn(ExperimentalMviKotlinApi::class)
class AuthStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    remoteAuthRepository: RemoteAuthRepository,
    mergedAuthRepository: MergedAuthRepository,
    localAccountRepository: LocalAccountRepository,
    localAccountDataRepository: LocalAccountDataRepository,
    mergedVendorAuthRepository: MergedVendorAuthRepository,
) : AuthStore,
    Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> by storeFactory.create<_, Action, Message, _, _>(
        name = "AuthStoreImpl",
        initialState = AuthStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    mergedVendorAuthRepository.getVendors().on {
                        val data = it.data
                        if (data != null) {
                            syncDispatch(
                                Message.SetVendors(
                                    data.vendors.map { vendor ->
                                        AuthStore.State.Vendor(
                                            region = vendor.region,
                                            realName = vendor.realName,
                                            vendor = vendor.vendor,
                                            host = vendor.host,
                                            devKey = vendor.devKey,
                                            logo = vendor.logo,
                                        )
                                    }.toPersistentList()
                                )
                            )
                        }
                    }
                }
            }
            onIntent<AuthStore.Intent.Confirm> {
                val selectedVendor = state.vendorSelector.selectedVendor
                val authField = state.authField
                if (state.isLoading || selectedVendor == null)
                    return@onIntent
                dispatch(Message.SetLoading(true))
                launch {
                    try {
                        val user = remoteAuthRepository.authorize(
                            username = authField.username,
                            password = authField.password,
                            vendor = selectedVendor.toData(),
                        ).toState(selectedVendor)
                        syncDispatch(Message.SetAuthorizedUser(user))
                    } catch (e: ErrorResponseException) {
                        syncDispatch(Message.SetAuthError(e))
                    }
                }
            }
            onIntent<AuthStore.Intent.EditUsername> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(Message.SetUsername(it.value))
            }
            onIntent<AuthStore.Intent.EditPassword> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(Message.SetPassword(it.value))
            }
            onIntent<AuthStore.Intent.SelectVendor> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(Message.SetSelectedVendor(it.value))
            }
            onIntent<AuthStore.Intent.ResetAuth> {
                dispatch(Message.SetConfirming(false))
            }
            onIntent<AuthStore.Intent.ConfirmCompleted> {
                val state = state
                val user = state.authorizedUser ?: return@onIntent
                dispatch(Message.SetLoading(true))
                launch {
                    localAccountRepository.addAccount(AccountId(user.id))
                    localAccountDataRepository.setData(
                        AccountData(
                            accountId = user.id,
                            name = user.title,
                            gender = user.gender.toState(),
                            classes = user.classes.map {
                                AccountData.Class(it.fullTitle)
                            },
                        )
                    )
                    mergedAuthRepository.addAuthorization(
                        Authorization(
                            token = user.token,
                            accountId = user.id,
                            vendor = user.vendor.toData(),
                        )
                    )
                    syncDispatch(Message.SetCompleted)
                }
            }
        },
        reducer = {
            when (it) {
                is Message.SetLoading -> copy(isLoading = it.value)
                is Message.SetConfirming -> copy(
                    isConfirming = it.value,
                    isContinueAvailable = !it.value,
                    isLoading = it.value
                )

                is Message.SetPassword -> {
                    val newAuthField = authField.copy(
                        password = it.value.filterNot(Char::isWhitespace),
                        error = null,
                    )
                    copy(
                        authField = newAuthField,
                        isContinueAvailable = isContinueAvailable(authField = newAuthField),
                    )
                }

                is Message.SetUsername -> {
                    val newAuthField = authField.copy(
                        username = it.value.filterNot(Char::isWhitespace),
                        error = null,
                    )
                    copy(
                        authField = newAuthField,
                        isContinueAvailable = isContinueAvailable(authField = newAuthField),
                    )
                }

                Message.SetCompleted -> copy(isCompleted = true, isLoading = false)
                is Message.SetAuthorizedUser -> copy(
                    isLoading = true,
                    authorizedUser = it.value,
                    isConfirming = true,
                    isContinueAvailable = false,
                )

                is Message.SetAuthError -> {
                    val newAuthField = authField.copy(
                        error = it.value.message ?: "unknown",
                    )
                    copy(
                        isLoading = false,
                        authorizedUser = null,
                        isConfirming = false,
                        authField = newAuthField,
                        isContinueAvailable = isContinueAvailable(authField = newAuthField),
                    )
                }

                is Message.SetVendors -> copy(
                    vendorSelector = vendorSelector.copy(
                        isLoading = false,
                        vendors = it.value,
                    )
                )

                is Message.SetSelectedVendor -> {
                    val newVendorSelector = vendorSelector.copy(selectedVendor = it.value)
                    val newAuthField = authField.copy(error = null)
                    copy(
                        vendorSelector = newVendorSelector,
                        authField = newAuthField,
                        isContinueAvailable = isContinueAvailable(
                            authField = newAuthField,
                            vendorSelector = newVendorSelector
                        ),
                    )
                }
            }
        },
    ) {


    private sealed interface Message {

        data class SetLoading(
            val value: Boolean,
        ) : Message

        data class SetConfirming(
            val value: Boolean,
        ) : Message


        data class SetSelectedVendor(
            val value: AuthStore.State.Vendor,
        ) : Message

        object SetCompleted : Message

        data class SetUsername(
            val value: String,
        ) : Message

        data class SetPassword(
            val value: String,
        ) : Message

        data class SetAuthorizedUser(
            val value: AuthStore.State.User,
        ) : Message

        data class SetAuthError(
            val value: Exception,
        ) : Message

        data class SetVendors(
            val value: ImmutableList<AuthStore.State.Vendor>,
        ) : Message

    }

    private sealed interface Action {

        object Setup : Action

    }

}

private fun UserOutput.toState(vendor: AuthStore.State.Vendor): AuthStore.State.User {
    return AuthStore.State.User(
        title = title,
        id = id,
        token = token,
        gender = gender.toState(),
        classes = classes.map {
            AuthStore.State.Class(
                fullTitle = it.fullTitle,
            )
        },
        vendor = vendor,
    )
}

private fun AuthStore.State.Vendor.toData(): Vendor {
    return Vendor(
        region = region,
        realName = realName,
        vendor = vendor,
        host = host,
        devKey = devKey,
        logo = logo,
    )
}

private fun AuthStore.State.User.Gender.toState(): Gender {
    return when (this) {
        AuthStore.State.User.Gender.MALE -> Gender.MALE
        AuthStore.State.User.Gender.FEMALE -> Gender.FEMALE
    }
}

private fun UserOutput.Gender.toState(): AuthStore.State.User.Gender {
    return when (this) {
        UserOutput.Gender.MALE -> AuthStore.State.User.Gender.MALE
        UserOutput.Gender.FEMALE -> AuthStore.State.User.Gender.FEMALE
    }
}


private fun AuthStore.State.isContinueAvailable(
    authField: AuthStore.State.AuthField = this.authField,
    vendorSelector: AuthStore.State.VendorSelector = this.vendorSelector,
): Boolean {
    return !isConfirming && !isLoading && authField.username.isNotBlank() && authField.password.isNotBlank() && authField.error == null && vendorSelector.selectedVendor != null
}