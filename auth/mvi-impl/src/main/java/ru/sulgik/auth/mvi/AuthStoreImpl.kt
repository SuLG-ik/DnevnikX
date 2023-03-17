package ru.sulgik.auth.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.LocalAccountDataRepository
import ru.sulgik.account.domain.LocalAccountRepository
import ru.sulgik.account.domain.data.Account
import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.auth.domain.LocalAuthRepository
import ru.sulgik.auth.domain.RemoteAuthRepository
import ru.sulgik.auth.domain.data.Authorization
import ru.sulgik.common.ErrorResponseException
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch
import ru.sulgik.dnevnikx.mvi.auth.AuthStore

@OptIn(ExperimentalMviKotlinApi::class)
class AuthStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    remoteAuthRepository: RemoteAuthRepository,
    localAuthRepository: LocalAuthRepository,
    localAccountRepository: LocalAccountRepository,
    localAccountDataRepository: LocalAccountDataRepository,
) : AuthStore,
    Store<AuthStore.Intent, AuthStore.State, AuthStore.Label> by storeFactory.create<_, Nothing, _, _, _>(
        name = "AuthStoreImpl",
        initialState = AuthStore.State(),
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onIntent<AuthStore.Intent.Confirm> {
                if (state.isLoading)
                    return@onIntent
                dispatch(state.copy(isLoading = true))
                launch {
                    try {
                        val user = remoteAuthRepository.authorize(state.username, state.password)
                        syncDispatch(
                            state.copy(
                                isLoading = false,
                                authorizedUser = AuthStore.State.User(
                                    title = user.title,
                                    id = user.id,
                                    token = user.token
                                ),
                                isConfirming = true,
                            )
                        )
                    } catch (e: ErrorResponseException) {
                        syncDispatch(
                            state.copy(
                                isLoading = false,
                                authorizedUser = null,
                                isConfirming = false,
                                error = e.error
                            )
                        )
                    }
                }
            }
            onIntent<AuthStore.Intent.EditUsername> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(
                    state.copy(
                        username = it.value.filterNot(Char::isWhitespace),
                        error = null
                    )
                )
            }
            onIntent<AuthStore.Intent.EditPassword> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(
                    state.copy(
                        password = it.value.filterNot(Char::isWhitespace),
                        error = null
                    )
                )
            }
            onIntent<AuthStore.Intent.ResetAuth> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(state.copy(isConfirming = false))
            }
            onIntent<AuthStore.Intent.ConfirmCompleted> {
                val state = state
                val user = state.authorizedUser
                if (state.isLoading || user == null)
                    return@onIntent
                dispatch(state.copy(isLoading = true))
                launch {
                    localAccountRepository.addAccount(Account(user.id))
                    localAccountDataRepository.setData(
                        AccountData(
                            accountId = user.id,
                            name = user.title,
                        )
                    )
                    localAuthRepository.addAuthorization(
                        Authorization(
                            token = user.token,
                            accountId = user.id,
                        )
                    )
                    syncDispatch(state.copy(isCompleted = true, isLoading = false))
                }
            }
        },
        reducer = directReducer(),
    )