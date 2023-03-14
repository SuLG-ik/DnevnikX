package ru.sulgik.main.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.application.component.ApplicationComponent
import ru.sulgik.auth.component.AuthComponent
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.diChildStack
import ru.sulgik.core.getParameterizedStore
import ru.sulgik.core.withAuth
import ru.sulgik.main.mvi.MainStore
import ru.sulgik.ui.component.Content

class MainComponent(
    componentContext: DIComponentContext,
    authScope: AuthScope?,
) : BaseComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = diChildStack(
        source = navigation,
        initialConfiguration = if (authScope == null) Config.Auth() else Config.Application(
            authScope
        ),
        key = "MainRouter",
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private val store: MainStore = getParameterizedStore { MainStore.Params(authScope) }

    private fun onAuthenticated(authScope: AuthScope) {
        store.accept(MainStore.Intent.ReAuth(authScope))
        scope.declare(authScope, allowOverride = true)
        navigation.replaceAll(Config.Application(authScope))
    }

    private fun onAuthorizationBack() {
        navigation.pop()
    }

    private fun onAddAccount() {
        navigation.push(Config.Auth(isBackAvailable = true))
    }

    private fun createChild(
        config: Config,
        componentContext: DIComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.Auth -> AuthComponent(
                componentContext,
                this::onAuthenticated,
                isBackAvailable = config.isBackAvailable,
                onBack = this::onAuthorizationBack,
            )

            is Config.Application -> ApplicationComponent(
                componentContext = componentContext.withAuth(scope = config.authScope),
                onReAuth = this::onAuthenticated,
                onAddAccount = this::onAddAccount,
            )
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        childStack.Content(modifier = modifier)
    }

    override val isLoading: Boolean
        get() = childStack.value.active.instance.isLoading

    sealed interface Config : Parcelable {

        @Parcelize
        data class Auth(
            val isBackAvailable: Boolean = false,
        ) : Config

        @Parcelize
        data class Application(
            val authScope: AuthScope,
        ) : Config

    }


}