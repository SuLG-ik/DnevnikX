package ru.sulgik.main.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.LocalSessionAccountRepository
import ru.sulgik.account.domain.data.AccountSession
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch


@OptIn(ExperimentalMviKotlinApi::class)
class MainStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    sessionAccountRepository: LocalSessionAccountRepository,
    params: MainStore.Params,
) : MainStore,
    Store<MainStore.Intent, MainStore.State, MainStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "MainStoreImpl",
        initialState = MainStore.State(params.authScope),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    sessionAccountRepository.updateLastAccountSession(params.authScope?.let { authScope ->
                        AccountSession(authScope.id)
                    })
                }
            }
            onIntent<MainStore.Intent.ReAuth> {
                launch {
                    sessionAccountRepository.updateLastAccountSession(AccountSession(it.authScope.id))
                    syncDispatch(MainStore.State(it.authScope))
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}