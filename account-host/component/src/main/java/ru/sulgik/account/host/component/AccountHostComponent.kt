package ru.sulgik.account.host.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.about.component.AboutComponent
import ru.sulgik.account.component.AccountComponent
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.authChildStack
import ru.sulgik.experimentalsettings.component.ExperimentalSettingsComponent
import ru.sulgik.finalmarks.component.FinalMarksComponent
import ru.sulgik.marksupdates.component.MarksUpdatesComponent
import ru.sulgik.schedule.host.component.ScheduleHostComponent
import ru.sulgik.ui.component.LocalNestedChildrenStackAnimator
import ru.sulgik.ui.component.NamedConfig
import ru.sulgik.ui.component.TrackedContent

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
        navigation.bringToFront(Config.MarksUpdates)
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
                componentContext = componentContext,
                isBackAvailable = true,
                onBack = this::onBack,
                onExperimentalSettings = this::onExperimentalSettings,
            )

            is Config.Profile -> AccountComponent(
                componentContext = componentContext,
                onSchedule = this::onSchedule,
                onAbout = this::onAbout,
                onUpdates = this::onUpdates,
                onFinalMarks = this::onFinalMarks,
                onSelectAccount = onSelectAccount,
            )

            Config.Schedule -> ScheduleHostComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )

            Config.FinalMarks -> FinalMarksComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )

            Config.ExperimentalSettings -> ExperimentalSettingsComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )

            Config.MarksUpdates -> MarksUpdatesComponent(
                componentContext = componentContext,
                backAvailable = true,
                onBack = this::onBack,
            )
        }
    }

    private fun onExperimentalSettings() {

    }

    @Composable
    override fun Content(modifier: Modifier) {
        val animator = LocalNestedChildrenStackAnimator.current
        childStack.TrackedContent(
            modifier = modifier,
            animation = stackAnimation { _, _, _ -> animator })
    }

    private sealed interface Config : NamedConfig {

        @Parcelize
        object Profile : Config {
            override val screenName: String
                get() = "profile"
        }

        @Parcelize
        object About : Config {
            override val screenName: String
                get() = "about"
        }

        @Parcelize
        object Schedule : Config {
            override val screenName: String
                get() = "schedule"
        }

        @Parcelize
        object FinalMarks : Config {
            override val screenName: String
                get() = "final_marks"
        }

        @Parcelize
        object ExperimentalSettings : Config {
            override val screenName: String
                get() = "experimental_settings"
        }

        @Parcelize
        object MarksUpdates : Config {
            override val screenName: String
                get() = "marks_updates"
        }

    }

}