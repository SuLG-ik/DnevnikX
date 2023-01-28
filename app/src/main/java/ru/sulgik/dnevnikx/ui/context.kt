package ru.sulgik.dnevnikx.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigationSource
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.Store
import org.koin.core.component.KoinScopeComponent
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.ParametrizedStore
import kotlin.reflect.KClass

interface DIComponentContext : ComponentContext, KoinScopeComponent {

    fun <T : Store<*, *, *>> DIComponentContext.getStore(
        clazz: KClass<T>,
        params: ParametersDefinition? = null,
    ): T

}

interface AuthorizedComponentContext : DIComponentContext {
    val authScope: AuthScope
}

private class DefaultAuthorizedComponentContext(
    private val componentContext: DIComponentContext,
    override val authScope: AuthScope,
) : AuthorizedComponentContext, DIComponentContext by componentContext {

    override fun <T : Store<*, *, *>> DIComponentContext.getStore(
        clazz: KClass<T>,
        params: ParametersDefinition?,
    ): T {
        return componentContext.getStore(clazz) {
                if (params == null)
                    parametersOf(authScope)
                else
                    parametersOf(authScope, *params().values.toTypedArray())
            }
    }
}




private class DefaultDIComponentContext(
    componentContext: ComponentContext,
    override val scope: Scope,
) : DIComponentContext, ComponentContext by componentContext {

    override fun <T : Store<*, *, *>> DIComponentContext.getStore(
        clazz: KClass<T>,
        params: ParametersDefinition?,
    ): T {
        if (clazz.java.isAssignableFrom(ParametrizedStore::class.java)) {
            throw IllegalArgumentException("Store with $clazz is parametrized and you should use DIComponentContext.getParameterizedStore(param")
        }
        return instanceKeeper.getStore(clazz) { scope.get(clazz = clazz, parameters = params) }
    }

}

fun ComponentContext.withDI(scope: Scope): DIComponentContext {
    return DefaultDIComponentContext(this, scope)
}

fun DIComponentContext.withAuth(scope: AuthScope): AuthorizedComponentContext {
    return DefaultAuthorizedComponentContext(this, scope)
}

fun DIComponentContext.childDIContext(
    key: String,
    lifecycle: Lifecycle? = null,
): DIComponentContext {
    return childContext(key, lifecycle).withDI(scope)
}

inline fun <C : Parcelable, T : Any> DIComponentContext.diChildStack(
    source: StackNavigationSource<C>,
    noinline initialStack: () -> List<C>,
    configurationClass: KClass<out C>,
    key: String = "DefaultChildStack",
    handleBackButton: Boolean = false,
    crossinline childFactory: (configuration: C, DIComponentContext) -> T,
): Value<ChildStack<C, T>> {
    return childStack(
        source = source,
        initialStack = initialStack,
        configurationClass = configurationClass,
        key = key,
        handleBackButton = handleBackButton
    ) { configuration, componentContext ->
        childFactory(configuration, componentContext.withDI(scope))
    }
}

inline fun <reified C : Parcelable, T : Any> DIComponentContext.diChildStack(
    source: StackNavigationSource<C>,
    noinline initialStack: () -> List<C>,
    key: String = "DefaultRouter",
    handleBackButton: Boolean = false,
    noinline childFactory: (configuration: C, DIComponentContext) -> T,
): Value<ChildStack<C, T>> =
    diChildStack(
        source = source,
        initialStack = initialStack,
        configurationClass = C::class,
        key = key,
        handleBackButton = handleBackButton,
        childFactory = childFactory,
    )

inline fun <reified C : Parcelable, T : Any> DIComponentContext.diChildStack(
    source: StackNavigationSource<C>,
    initialConfiguration: C,
    key: String = "DefaultRouter",
    handleBackButton: Boolean = false,
    noinline childFactory: (configuration: C, DIComponentContext) -> T,
): Value<ChildStack<C, T>> =
    diChildStack(
        source = source,
        initialStack = { listOf(initialConfiguration) },
        configurationClass = C::class,
        key = key,
        handleBackButton = handleBackButton,
        childFactory = childFactory,
    )


fun AuthorizedComponentContext.childAuthContext(
    key: String,
    lifecycle: Lifecycle? = null,
): AuthorizedComponentContext {
    return childDIContext(key, lifecycle).withAuth(authScope)
}

inline fun <C : Parcelable, T : Any> AuthorizedComponentContext.authChildStack(
    source: StackNavigationSource<C>,
    noinline initialStack: () -> List<C>,
    configurationClass: KClass<out C>,
    key: String = "DefaultChildStack",
    handleBackButton: Boolean = false,
    crossinline childFactory: (configuration: C, AuthorizedComponentContext) -> T,
): Value<ChildStack<C, T>> {
    return diChildStack(
        source = source,
        initialStack = initialStack,
        configurationClass = configurationClass,
        key = key,
        handleBackButton = handleBackButton,
        childFactory = { configuration: C, componentContext: DIComponentContext ->
            childFactory(configuration, componentContext.withAuth(authScope))
        }
    )
}

inline fun <reified C : Parcelable, T : Any> AuthorizedComponentContext.authChildStack(
    source: StackNavigationSource<C>,
    noinline initialStack: () -> List<C>,
    key: String = "DefaultRouter",
    handleBackButton: Boolean = false,
    noinline childFactory: (configuration: C, AuthorizedComponentContext) -> T,
): Value<ChildStack<C, T>> =
    authChildStack(
        source = source,
        initialStack = initialStack,
        configurationClass = C::class,
        key = key,
        handleBackButton = handleBackButton,
        childFactory = childFactory,
    )

inline fun <reified C : Parcelable, T : Any> AuthorizedComponentContext.authChildStack(
    source: StackNavigationSource<C>,
    initialConfiguration: C,
    key: String = "DefaultRouter",
    handleBackButton: Boolean = false,
    noinline childFactory: (configuration: C, AuthorizedComponentContext) -> T,
): Value<ChildStack<C, T>> =
    authChildStack(
        source = source,
        initialStack = { listOf(initialConfiguration) },
        configurationClass = C::class,
        key = key,
        handleBackButton = handleBackButton,
        childFactory = childFactory,
    )

