package ru.sulgik.dnevnikx.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.data.AuthScope
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
        initialConfiguration = if (authScope == null) Config.Auth else Config.Application(authScope),
        key = "MainRouter",
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private fun onAuthenticated(authScope: AuthScope) {
        scope.declare(authScope, allowOverride = true)
        navigation.replaceAll(Config.Application(authScope))
    }

    private fun createChild(
        config: Config,
        componentContext: DIComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.Auth -> AuthComponent(componentContext, this::onAuthenticated)
            is Config.Application -> ApplicationComponent(componentContext.withAuth(config.authScope))
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
        data object Auth : Config

        @Parcelize
        data class Application(
            val authScope: AuthScope,
        ) : Config

    }


}