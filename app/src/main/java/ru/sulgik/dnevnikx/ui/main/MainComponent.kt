package ru.sulgik.dnevnikx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.getParameterizedStore
import ru.sulgik.dnevnikx.mvi.main.MainStore
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.application.ApplicationComponent
import ru.sulgik.dnevnikx.ui.auth.AuthComponent
import ru.sulgik.dnevnikx.ui.diChildStack
import ru.sulgik.dnevnikx.ui.withAuth

class MainComponent(
    componentContext: DIComponentContext,
    authScope: AuthScope?,
) : BaseComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = diChildStack(
        source = navigation,
        initialConfiguration = if (authScope == null) Config.Auth() else Config.Application(authScope),
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

    private fun onAuthenticated(account: Account) {
        onAuthenticated(AuthScope(account.id))
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