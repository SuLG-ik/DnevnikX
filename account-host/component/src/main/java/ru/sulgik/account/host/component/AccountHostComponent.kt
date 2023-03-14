package ru.sulgik.account.host.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.about.component.AboutComponent
import ru.sulgik.account.component.AccountComponent
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.authChildStack
import ru.sulgik.finalmarks.component.FinalMarksComponent
import ru.sulgik.schedule.component.ScheduleComponent
import ru.sulgik.ui.component.Content

class AccountHostComponent(
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

    private fun onFinalMarks() {
        navigation.bringToFront(Config.FinalMarks)
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
                onFinalMarks = this::onFinalMarks,
                onSelectAccount = onSelectAccount,
            )

            Config.Schedule -> ScheduleComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )

            Config.FinalMarks -> FinalMarksComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        childStack.Content(modifier = modifier, animation = null)
    }

    private sealed interface Config : Parcelable {

        @Parcelize
        object Profile : Config

        @Parcelize
        object About : Config

        @Parcelize
        object Schedule : Config

        @Parcelize
        object FinalMarks : Config

    }

}