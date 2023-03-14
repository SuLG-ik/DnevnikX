package ru.sulgik.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.mvikotlin.core.store.Store
import org.koin.core.parameter.ParametersDefinition
import ru.sulgik.auth.core.AuthScope
import kotlin.reflect.KClass

abstract class BaseComponentContext(componentContext: DIComponentContext) :
    DIComponentContext by componentContext {

    open val isLoading: Boolean get() = false

    @Composable
    abstract fun Content(modifier: Modifier)

}

abstract class BaseAuthorizedComponentContext(private val componentContext: AuthorizedComponentContext) :
    BaseComponentContext(componentContext), AuthorizedComponentContext {

    override val authScope: AuthScope = componentContext.authScope

    override fun <T : Store<*, *, *>> DIComponentContext.getStore(
        clazz: KClass<T>,
        params: ParametersDefinition?
    ): T {
        return componentContext.getStore(clazz, params)
    }

}