package ru.sulgik.about.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import ru.sulgik.about.domain.data.AboutOutput
import ru.sulgik.about.domain.data.BuiltInAboutRepository
import ru.sulgik.core.directReducer

@OptIn(ExperimentalMviKotlinApi::class)
class AboutStoreImpl(
    storeFactory: StoreFactory,
    coroutineDispatcher: CoroutineDispatcher,
    builtInAboutRepository: BuiltInAboutRepository,
) : AboutStore,
    Store<AboutStore.Intent, AboutStore.State, AboutStore.Label> by storeFactory.create<_, Action, _, _, _>(
        name = "AboutStoreImpl",
        initialState = AboutStore.State(),
        bootstrapper = coroutineBootstrapper(coroutineDispatcher) {
            dispatch(Action.Setup)
        },
        executorFactory = coroutineExecutorFactory(coroutineDispatcher) {
            onAction<Action.Setup> {
                val data = builtInAboutRepository.getAboutData().toState()
                dispatch(AboutStore.State(data))
            }
        },
        reducer = directReducer(),
    ) {

    private sealed interface Action {
        object Setup : Action
    }

}

private fun AboutOutput.toState(): AboutStore.State.AboutData {
    return AboutStore.State.AboutData(
        application = AboutStore.State.AboutData.ApplicationData(
            name = application.name,
            version = application.version,
        ),
        developer = AboutStore.State.AboutData.DeveloperData(
            name = developer.name,
            uri = developer.uri,
        ),
        domain = AboutStore.State.AboutData.DomainInfo(
            name = domain.name,
            domain = domain.domain,
            uri = domain.uri,
        )
    )
}
