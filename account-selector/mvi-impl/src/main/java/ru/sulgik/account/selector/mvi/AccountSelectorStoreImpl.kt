package ru.sulgik.account.selector.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch
import ru.sulgik.account.domain.LocalAccountDataRepository
import ru.sulgik.account.domain.LocalAccountRepository

@OptIn(ExperimentalMviKotlinApi::class)
class AccountSelectorStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    localAccountRepository: LocalAccountRepository,
    localAccountDataRepository: LocalAccountDataRepository,
) : AccountSelectorStore,
    Store<AccountSelectorStore.Intent, AccountSelectorStore.State, AccountSelectorStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "AccountSelectorStoreImpl",
        initialState = AccountSelectorStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                localAccountRepository.getAccounts()
                    .onEach {
                        val data = localAccountDataRepository.getData(it)
                        val accounts = data.map { account ->
                            AccountSelectorStore.State.Account(
                                id = account.accountId,
                                name = account.name,
                                selected = authScope.id == account.accountId,
                            )
                        }
                        syncDispatch(
                            state.copy(
                                accounts = accounts,
                                isLoading = false
                            )
                        )
                    }
                    .flowOn(coroutineDispatcher)
                    .launchIn(this)
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}