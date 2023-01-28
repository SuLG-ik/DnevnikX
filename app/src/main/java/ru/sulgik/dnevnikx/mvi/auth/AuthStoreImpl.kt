package ru.sulgik.dnevnikx.mvi.auth

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.repository.auth.Authorization
import ru.sulgik.dnevnikx.repository.auth.LocalAuthRepository
import ru.sulgik.dnevnikx.repository.auth.RemoteAuthRepository

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [AuthStore::class])
class AuthStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    remoteAuthRepository: RemoteAuthRepository,
    localAuthRepository: LocalAuthRepository,
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
                    val user = remoteAuthRepository.authorize(state.username, state.password)
                    syncDispatch(
                        state.copy(
                            isLoading = false,
                            authorizedUser = user,
                            isConfirming = true,
                        )
                    )
                }
            }
            onIntent<AuthStore.Intent.EditUsername> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(state.copy(username = it.value.filterNot(Char::isWhitespace)))
            }
            onIntent<AuthStore.Intent.EditPassword> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(state.copy(password = it.value.filterNot(Char::isWhitespace)))
            }
            onIntent<AuthStore.Intent.ResetAuth> {
                val state = state
                if (state.isLoading)
                    return@onIntent
                dispatch(state.copy(isConfirming = false))
            }
            onIntent<AuthStore.Intent.ConfirmCompleted> {
                val state = state
                if (state.isLoading || state.authorizedUser == null)
                    return@onIntent
                dispatch(state.copy(isLoading = true))
                launch {
                    localAuthRepository.setAuthorization(
                        Authorization(
                            token = state.authorizedUser.token,
                            id = state.authorizedUser.id,
                        )
                    )
                    syncDispatch(state.copy(isCompleted = true, isLoading = false))
                }
            }
        },
        reducer = directReducer(),
    ) {


}
