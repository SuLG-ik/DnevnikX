package ru.sulgik.dnevnikx.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.about.AboutComponent
import ru.sulgik.dnevnikx.ui.authChildStack
import ru.sulgik.dnevnikx.ui.schedule.ScheduleComponent

class ProfileHostComponent(
    componentContext: AuthorizedComponentContext,
    private val onSelectAccount: () -> Unit,
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

    private fun onSchedule() {
        navigation.bringToFront(Config.Schedule)
    }

    private fun onUpdates() {
        /* TODO */
    }

    private fun onBack() {
        navigation.pop()
    }

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.About -> AboutComponent(
                componentContext,
                isBackAvailable = true,
                onBack = this::onBack
            )

            is Config.Profile -> AccountComponent(
                componentContext = componentContext,
                onSchedule = this::onSchedule,
                onAbout = this::onAbout,
                onUpdates = this::onUpdates,
                onSelectAccount = onSelectAccount,
            )

            Config.Schedule -> ScheduleComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
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

        @Parcelize
        object Schedule : Config

    }

}