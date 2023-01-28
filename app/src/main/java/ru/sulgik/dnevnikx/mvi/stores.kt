package ru.sulgik.dnevnikx.mvi

import androidx.compose.runtime.mutableStateOf
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.statekeeper.consume
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import ru.sulgik.dnevnikx.ui.DIComponentContext
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMviKotlinApi::class)
suspend fun <Message : Any> CoroutineExecutorScope<*, Message, *>.syncDispatch(message: Message) {
    withContext(Dispatchers.Main) {
        dispatch(message)
    }
}

@OptIn(ExperimentalMviKotlinApi::class)
fun <Message : Any> CoroutineExecutorScope<*, Message, *>.dispatchLaunched(message: suspend CoroutineScope.() -> Message) {
    launch {
        syncDispatch(message())
    }
}

@OptIn(ExperimentalMviKotlinApi::class)
suspend fun <Label : Any> CoroutineExecutorScope<*, *, Label>.syncPublish(message: Label) {
    withContext(Dispatchers.Main) {
        publish(message)
    }
}

abstract class SyncCoroutineExecutor<in Intent : Any, in Action : Any, in State : Any, Message : Any, Label : Any>(
    mainContext: CoroutineContext = Dispatchers.Main,
) : CoroutineExecutor<Intent, Action, State, Message, Label>(mainContext) {

    suspend fun syncDispatch(message: Message) {
        withContext(Dispatchers.Main) {
            dispatch(message)
        }
    }

    suspend fun syncPublish(label: Label) {
        withContext(Dispatchers.Main) {
            publish(label)
        }
    }

}

fun <T : Any> directReducer(): Reducer<T, T> {
    return Reducer { it }
}

interface ParametrizedStore<in Intent : Any, out State : Any, out Label : Any, in Params : Any> :
    Store<Intent, State, Label>

internal inline fun <reified T : Store<*, *, *>> DIComponentContext.getStore(
    noinline params: ParametersDefinition? = null,
): T {
//    if (T::class.java.isAssignableFrom(ParametrizedStore::class.java)) {
//        throw IllegalArgumentException("Store with ${T::class} is parametrized and you should use DIComponentContext.getParameterizedStore(param")
//    }
    return getStore(T::class, params)
}

internal inline fun <Param : Any, reified T : ParametrizedStore<*, *, *, Param>> DIComponentContext.getParameterizedStore(
    crossinline param: () -> Param,
): T {
    return getStore { parametersOf(param()) }
}


internal inline fun <Param : Any, reified T : ParametrizedStore<*, *, *, Param>> DIComponentContext.getParameterizedSavedStateStore(
    crossinline param: () -> Param,
): T {
    return getParameterizedStore(param)
}

internal inline fun <Param : Any, reified T : ParametrizedStore<*, *, *, Param>> DIComponentContext.getParameterizedStore(
    param: Param,
): T = getParameterizedStore { param }

internal inline fun <reified SavedState : Parcelable, reified State : Any, reified T : Store<*, State, *>> DIComponentContext.getSavedStateStore(
    key: String,
    crossinline save: (State) -> SavedState,
    crossinline restore: (SavedState) -> State,
): T {
    val store: T = getStore {
        val value = stateKeeper.consume<SavedState>(key)
        parametersOf(value?.let { restore(it) })
    }
    stateKeeper.register(key) { save(store.state) }
    return store
}


internal inline fun <reified State : Parcelable, reified T : Store<*, State, *>> DIComponentContext.getSavedStateStore(
    key: String,
): T {
    return getSavedStateStore(
        key,
        save = { it },
        restore = { it }
    )
}

fun <State : Any> Store<*, State, *>.states(lifecycleOwner: LifecycleOwner): androidx.compose.runtime.State<State> {
    return states(
        lifecycleOwner = lifecycleOwner,
        transform = { it },
    )
}

fun <State : Any, T : Any> Store<*, State, *>.states(
    lifecycleOwner: LifecycleOwner,
    transform: (State) -> T,
): androidx.compose.runtime.State<T> {
    return toState(
        lifecycleOwner,
        currentState = state,
        mapper = transform,
        Store<*, State, *>::states,
    )
}


private inline fun <T, R, C> T.toState(
    lifecycleOwner: LifecycleOwner,
    currentState: R,
    crossinline mapper: (R) -> C,
    crossinline subscribe: T.(Observer<R>) -> Disposable,
): androidx.compose.runtime.State<C> {
    val state = mutableStateOf(mapper(currentState))
    var disposable: Disposable? = null
    lifecycleOwner.lifecycle.subscribe(
        onCreate = {
            disposable = subscribe(observer(onNext = { state.value = mapper(it) }))
        },
        onDestroy = {
            disposable?.dispose()
        }
    )
    return state
}