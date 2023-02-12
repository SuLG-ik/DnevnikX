package ru.sulgik.dnevnikx.ui.proxy

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.mvi.getSavedStateStore
import ru.sulgik.dnevnikx.mvi.proxy.ProxyStore
import ru.sulgik.dnevnikx.mvi.states
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.diChildStack
import ru.sulgik.dnevnikx.ui.main.MainComponent
import ru.sulgik.dnevnikx.ui.splash.SplashComponent
import ru.sulgik.dnevnikx.ui.theme.DnevnikXTheme

class ProxyComponent(componentContext: DIComponentContext) :
    BaseComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val store: ProxyStore = getSavedStateStore("proxy_store")

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
        DnevnikXTheme {
            Surface {
                childStack.Content(
                    modifier = modifier,
                    animation = null,
                )
            }
        }
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