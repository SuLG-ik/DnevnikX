package ru.sulgik.dnevnikx.mvi.account

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.sulgik.dnevnikx.BuildConfig
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.directReducer
import ru.sulgik.dnevnikx.mvi.syncDispatch
import ru.sulgik.dnevnikx.repository.account.LocalAccountDataRepository

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [AccountStore::class])
class AccountStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    localAccountDataRepository: LocalAccountDataRepository,
) : AccountStore,
    Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "AccountStoreImpl",
        initialState = AccountStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                launch {
                    val account = localAccountDataRepository.getData(Account(authScope.id))
                    val state = state
                    syncDispatch(
                        state.copy(
                            account = AccountStore.State.AccountData(
                                isLoading = false,
                                account = AccountStore.State.Account(account.name),
                            ),
                            actions = AccountStore.State.ActionsData(
                                isLoading = false,
                                actions = AccountStore.State.Actions(
                                    isScheduleAvailable = true,
                                    isUpdatesAvailable = true,
                                    AccountStore.State.AboutData(
                                        "DnevnikX ${BuildConfig.APP_VERSION}"
                                    ),
                                )
                            )
                        )
                    )
                }
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}