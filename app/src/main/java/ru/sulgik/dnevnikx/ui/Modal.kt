package ru.sulgik.dnevnikx.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.consume

@Parcelize
data class ModalState(val isVisible: Boolean) : Parcelable

interface Modal {

    val modalState: ModalState

    fun updateState(isVisible: Boolean)

    @Composable
    fun Content(modifier: Modifier)

}

abstract class ModalComponentContext(
    componentContext: DIComponentContext,
    private val initialState: ModalState = ModalState(false),
    private val onHide: () -> Unit = {},
) : BaseComponentContext(componentContext), Modal {

    private val _modalState by lazy {
        mutableStateOf(
            stateKeeper.consume("modal_state") ?: initialState
        ).apply {
            stateKeeper.register("modal_state", supplier = this::value::get)
        }
    }
    override var modalState: ModalState by _modalState

    override fun updateState(isVisible: Boolean) {
        if (!isVisible && modalState.isVisible) {
            onHide()
        }
        modalState = ModalState(isVisible)
    }

}

abstract class AuthorizedModalComponentContext(
    componentContext: AuthorizedComponentContext,
    private val initialState: ModalState = ModalState(false),
    private val onHide: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext), Modal {

    private val _modalState by lazy {
        mutableStateOf(
            stateKeeper.consume("modal_state") ?: initialState
        ).apply {
            stateKeeper.register("modal_state", supplier = this::value::get)
        }
    }
    override var modalState: ModalState by _modalState

    override fun updateState(isVisible: Boolean) {
        if (!isVisible && modalState.isVisible) {
            onHide()
        }
        modalState = ModalState(isVisible)
    }

}

abstract class StackModalComponentContext<Child : Any>(
    componentContext: DIComponentContext,
    initialState: ModalState = ModalState(false),
) :
    ModalComponentContext(componentContext, initialState) {

    abstract val modalStack: Value<ChildStack<*, Child>>

}