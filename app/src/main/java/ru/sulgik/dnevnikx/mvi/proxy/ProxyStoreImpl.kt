package ru.sulgik.dnevnikx.mvi.proxy

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.repository.auth.LocalAuthRepository

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [ProxyStore::class])
class ProxyStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    savedState: ProxyStore.State?,
    localAuthRepository: LocalAuthRepository,
) : ProxyStore,
    Store<ProxyStore.Intent, ProxyStore.State, ProxyStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "ProxyStoreImpl",
        initialState = savedState ?: ProxyStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            if (savedState == null)
                dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    val authorization = localAuthRepository.getAuthorizationOrNull()
                    if (authorization != null) {
                        syncDispatch(
                            ProxyStore.State(
                                authScope = AuthScope(id = authorization.id),
                                isLoading = false,
                            )
                        )
                    } else {
                        syncDispatch(ProxyStore.State(authScope = null, isLoading = false))
                    }
                }
            }
        },
        reducer = directReducer(),
    ) {


    sealed interface Action {
        object Setup : Action
    }

}