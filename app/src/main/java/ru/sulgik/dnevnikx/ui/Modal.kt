package ru.sulgik.dnevnikx.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.consume

@Parcelize
data class ModalState(val isVisible: Boolean) : Parcelable

abstract class ModalComponentContext(
    componentContext: DIComponentContext,
    private val initialState: ModalState = ModalState(false),
) : BaseComponentContext(componentContext) {


    private val _modalState by lazy {
        mutableStateOf(
            stateKeeper.consume("modal_state") ?: initialState
        ).apply {
            stateKeeper.register("modal_state", supplier = this::value::get)
        }
    }
    var modalState: ModalState by _modalState

    fun updateState(isVisible: Boolean) {
        modalState = ModalState(isVisible)
    }

}

abstract class StackModalComponentContext<Child : Any>(componentContext: DIComponentContext, initialState: ModalState = ModalState(false)) :
    ModalComponentContext(componentContext, initialState) {

    abstract val modalStack: Value<ChildStack<*, Child>>

}