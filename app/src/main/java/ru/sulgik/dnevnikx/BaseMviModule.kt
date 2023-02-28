package ru.sulgik.dnevnikx

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.logger.DefaultLogFormatter
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module()
class BaseMviModule {

    @Single
    fun bindDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

    @Single
    fun bindStoreFactory(): StoreFactory {
        return StrictStoreFactory(
            LoggingStoreFactory(
                DefaultStoreFactory(),
                logFormatter = DefaultLogFormatter(Int.MAX_VALUE)
            )
        )
    }

}


class StrictStoreFactory(private val delegate: StoreFactory) : StoreFactory {
    override fun <Intent : Any, Action : Any, Message : Any, State : Any, Label : Any> create(
        name: String?,
        autoInit: Boolean,
        initialState: State,
        bootstrapper: Bootstrapper<Action>?,
        executorFactory: () -> Executor<Intent, Action, State, Message, Label>,
        reducer: Reducer<State, Message>,
    ): Store<Intent, State, Label> {
        return StrictStore(
            delegate.create(
                name = name,
                autoInit = autoInit,
                initialState = initialState,
                bootstrapper = bootstrapper,
                executorFactory = executorFactory,
                reducer = reducer
            )
        )
    }

}

private class StrictStore<Intent : Any, State : Any, Label : Any>(
    private val delegate: Store<Intent, State, Label>,
) : Store<Intent, State, Label> {
    override val isDisposed: Boolean get() = delegate.isDisposed
    override val state: State
        get() {
            strictDisposed()
            return delegate.state
        }

    override fun dispose() {
        strictDisposed()
        delegate.dispose()
    }

    override fun init() {
        strictDisposed()
        delegate.init()
    }

    override fun labels(observer: com.arkivanov.mvikotlin.rx.Observer<Label>): com.arkivanov.mvikotlin.rx.Disposable {
        strictDisposed()
        return delegate.labels(observer)
    }

    override fun states(observer: com.arkivanov.mvikotlin.rx.Observer<State>): com.arkivanov.mvikotlin.rx.Disposable {
        strictDisposed()
        return delegate.states(observer)
    }

    override fun accept(intent: Intent) {
        strictDisposed()
        delegate.accept(intent)
    }

    private fun strictDisposed() {
        if (delegate.isDisposed)
            throw IllegalStateException("Store was disposed but still in use")
    }


}