package ru.sulgik.dnevnikx.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.about.AboutComponent
import ru.sulgik.dnevnikx.ui.authChildStack

class ProfileHostComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {


    private val navigation = StackNavigation<Config>()

    private val childStack = authChildStack(
        source = navigation,
        initialConfiguration = Config.Profile,
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private fun onAbout() {
        navigation.bringToFront(Config.About)
    }

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.About -> AboutComponent(componentContext)
            is Config.Profile -> ProfileComponent(
                componentContext,
                onSchedule = {},
                onAbout = this::onAbout,
                onUpdates = {},
            )
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        childStack.Content(modifier = modifier)
    }

    private sealed interface Config : Parcelable {

        @Parcelize
        object Profile : Config

        @Parcelize
        object About : Config

    }

}