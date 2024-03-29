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
import ru.sulgik.account.domain.CachedAccountDataRepository
import ru.sulgik.account.domain.data.Gender
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.directReducer
import ru.sulgik.core.syncDispatch
import ru.sulgik.kacher.core.on

@OptIn(ExperimentalMviKotlinApi::class)
@Factory(binds = [AccountStore::class])
class AccountStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    authScope: AuthScope,
    cachedAccountDataRepository: CachedAccountDataRepository,
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
                    val state = state

                    val accountResource =
                        cachedAccountDataRepository.getData(AuthScope(authScope.id))
                    val aboutResource = aboutRepository.getApplicationData()
                    accountResource.on {
                        val accountData = it.data
                        if (accountData != null) {
                            syncDispatch(
                                state.copy(
                                    account = AccountStore.State.AccountData(
                                        isLoading = false,
                                        account = AccountStore.State.Account(
                                            name = accountData.name,
                                            gender = accountData.gender.toState()
                                        ),
                                    ),
                                    actions = AccountStore.State.ActionsData(
                                        isLoading = false,
                                        actions = AccountStore.State.Actions(
                                            isScheduleAvailable = true,
                                            isUpdatesAvailable = true,
                                            isFinalMarksAvailable = true,
                                            aboutData = AccountStore.State.AboutData(
                                                applicationFullName = aboutResource.fullName,
                                            )
                                        )
                                    )
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

private fun Gender.toState(): AccountStore.State.Gender {
    return when (this) {
        Gender.MALE -> AccountStore.State.Gender.MALE
        Gender.FEMALE -> AccountStore.State.Gender.FEMALE
    }

}
