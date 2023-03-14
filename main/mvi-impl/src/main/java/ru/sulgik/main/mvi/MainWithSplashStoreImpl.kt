package ru.sulgik.main.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.LocalSessionAccountRepository
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.domain.LocalAuthRepository
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch

@OptIn(ExperimentalMviKotlinApi::class)
class MainWithSplashStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    savedState: MainWithSplashStore.State?,
    sessionAccountRepository: LocalSessionAccountRepository,
    localAuthRepository: LocalAuthRepository,
) : MainWithSplashStore,
    Store<MainWithSplashStore.Intent, MainWithSplashStore.State, MainWithSplashStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "MainWithSplashStoreImpl",
        initialState = savedState ?: MainWithSplashStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            if (savedState == null)
                dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    val lastSession = sessionAccountRepository.getLastAccountSession()
                    if (lastSession == null) {
                        syncDispatch(MainWithSplashStore.State(authScope = null, isLoading = false))
                        return@launch
                    }
                    val authorization = localAuthRepository.getAuthorization(lastSession.accountId)
                    syncDispatch(
                        MainWithSplashStore.State(
                            authScope = AuthScope(id = authorization.accountId),
                            isLoading = false,
                        )
                    )
                }
            }
        },
        reducer = directReducer(),
    ) {


    sealed interface Action {
        object Setup : Action
    }

}