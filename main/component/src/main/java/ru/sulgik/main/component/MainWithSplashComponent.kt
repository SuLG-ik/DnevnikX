package ru.sulgik.main.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.diChildStack
import ru.sulgik.core.getSavedStateStore
import ru.sulgik.core.states
import ru.sulgik.main.mvi.MainWithSplashStore
import ru.sulgik.splash.component.SplashComponent
import ru.sulgik.ui.component.Content

class MainWithSplashComponent(componentContext: DIComponentContext) :
    BaseComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val store: MainWithSplashStore = getSavedStateStore("proxy_store")

    private val childStack = diChildStack(
        source = navigation,
        initialConfiguration = store.state.let {
            if (it.authScope == null)
                Config.Splash
            else
                Config.Main(it.authScope)
        },
        key = "ProxyRouter",
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private val state = store.states(this) {
        if (!it.isLoading) {
            navigation.replaceAll(Config.Main(it.authScope))
        }
        it
    }

    private fun createChild(
        config: Config,
        componentContext: DIComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.Splash -> SplashComponent(componentContext)
            is Config.Main -> MainComponent(componentContext, config.authScope)
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        childStack.Content(
            modifier = modifier,
            animation = null,
        )
    }

    override val isLoading: Boolean
        get() = store.state.isLoading

    sealed interface Config : Parcelable {

        @Parcelize
        object Splash : Config

        @Parcelize
        data class Main(val authScope: AuthScope?) : Config

    }


}