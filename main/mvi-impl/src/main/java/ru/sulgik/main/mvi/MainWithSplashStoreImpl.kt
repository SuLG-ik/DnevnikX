package ru.sulgik.main.mvi

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.LocalSessionAccountRepository
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.auth.domain.MergedAuthRepository
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch

@OptIn(ExperimentalMviKotlinApi::class)
class MainWithSplashStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    savedState: MainWithSplashStore.State?,
    sessionAccountRepository: LocalSessionAccountRepository,
    localAuthRepository: MergedAuthRepository,
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
                launch(Dispatchers.Main) {
                    Log.d("pisos", "getting last session")
                    val lastSession = sessionAccountRepository.getLastAccountSession()
                    Log.d("pisos", "get last session")

                    if (lastSession == null) {
                        syncDispatch(MainWithSplashStore.State(authScope = null, isLoading = false))
                        return@launch
                    }
                    Log.d("pisos", "dispatching")
                    val authorization = localAuthRepository.getAuthorization(lastSession.accountId)
                    Log.d("pisos", "dispatch")
                    dispatch(
                        MainWithSplashStore.State(
                            authScope = AuthScope(id = authorization.accountId),
                            isLoading = false,
                        )
                    )
                    Log.d("pisos", "dispatched")

                }
            }
        },
        reducer = directReducer(),
    ) {


    sealed interface Action {
        object Setup : Action
    }

}