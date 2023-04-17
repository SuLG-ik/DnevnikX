package ru.sulgik.account.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import ru.sulgik.about.domain.data.BuiltInAboutRepository
import ru.sulgik.account.domain.LocalAccountDataRepository
import ru.sulgik.account.domain.data.Account
import ru.sulgik.account.domain.data.Gender
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [AccountStore::class])
class AccountStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    localAccountDataRepository: LocalAccountDataRepository,
    aboutRepository: BuiltInAboutRepository,
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
                    val aboutData = aboutRepository.getAboutData()
                    syncDispatch(
                        state.copy(
                            account = AccountStore.State.AccountData(
                                isLoading = false,
                                account = AccountStore.State.Account(
                                    name = account.name,
                                    gender = account.gender.toState()
                                ),
                            ),
                            actions = AccountStore.State.ActionsData(
                                isLoading = false,
                                actions = AccountStore.State.Actions(
                                    isScheduleAvailable = true,
                                    isUpdatesAvailable = true,
                                    isFinalMarksAvailable = true,
                                    AccountStore.State.AboutData(
                                        applicationFullName = aboutData.application.fullName,
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

private fun Gender.toState(): AccountStore.State.Gender {
    return when (this) {
        Gender.MALE -> AccountStore.State.Gender.MALE
        Gender.FEMALE -> AccountStore.State.Gender.FEMALE
    }

}
