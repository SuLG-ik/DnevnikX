package ru.sulgik.account.selector.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.sulgik.account.domain.CachedAccountDataRepository
import ru.sulgik.account.domain.LocalAccountRepository
import ru.sulgik.account.domain.data.Gender
import ru.sulgik.account.domain.data.toAuthScope
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.kacher.core.on

@OptIn(ExperimentalMviKotlinApi::class)
class AccountSelectorStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    localAccountRepository: LocalAccountRepository,
    localAccountDataRepository: CachedAccountDataRepository,
) : AccountSelectorStore,
    Store<AccountSelectorStore.Intent, AccountSelectorStore.State, AccountSelectorStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "AccountSelectorStoreImpl",
        initialState = AccountSelectorStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch(Dispatchers.Main) {
                    localAccountRepository.getAccounts().collectLatest { it ->
                        localAccountDataRepository.getData(it.map { it.toAuthScope() })
                            .on { status ->
                                val accounts = status.data?.map { account ->
                                    AccountSelectorStore.State.Account(
                                        id = account.accountId,
                                        name = account.name,
                                        selected = authScope.id == account.accountId,
                                        gender = account.gender.toState()
                                    )
                                }
                                dispatch(
                                    state.copy(
                                        accounts = accounts,
                                        selectedAccount = accounts?.firstOrNull { authScope.id == it.id },
                                        isLoading = false
                                    )
                                )
                            }
                    }
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}

private fun Gender.toState(): AccountSelectorStore.State.Gender {
    return when (this) {
        Gender.MALE -> AccountSelectorStore.State.Gender.MALE
        Gender.FEMALE -> AccountSelectorStore.State.Gender.FEMALE
    }
}
