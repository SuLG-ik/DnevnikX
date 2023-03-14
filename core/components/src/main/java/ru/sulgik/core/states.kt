package ru.sulgik.core


import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleOwner
import com.arkivanov.essenty.lifecycle.subscribe

fun <State : Any> Value<State>.states(lifecycleOwner: LifecycleOwner): androidx.compose.runtime.State<State> {
    return states(
        lifecycleOwner = lifecycleOwner,
        transform = { it },
    )
}

fun <State : Any, T : Any> Value<State>.states(
    lifecycleOwner: LifecycleOwner,
    transform: (State) -> T,
): androidx.compose.runtime.State<T> {
    return toState(
        lifecycleOwner = lifecycleOwner,
        currentState = value,
        mapper = transform,
        subscribe = this::subscribe,
        unsubscribe = this::unsubscribe,
    )
}


private inline fun <R, C> toState(
    lifecycleOwner: LifecycleOwner,
    currentState: R,
    crossinline mapper: (R) -> C,
    crossinline subscribe: ((R) -> Unit) -> Unit,
    crossinline unsubscribe: ((R) -> Unit) -> Unit,
): androidx.compose.runtime.State<C> {
    val state = mutableStateOf(mapper(currentState))
    val observer = { value: R -> state.value = mapper(value) }
    lifecycleOwner.lifecycle.subscribe(
        onCreate = {
            subscribe(observer)
        },
        onDestroy = {
            unsubscribe(observer)
        }
    )
    return state
}
