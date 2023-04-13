package ru.sulgik.marks.host.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.DatePeriodParcelable
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.authChildStack
import ru.sulgik.marks.component.MarksComponent
import ru.sulgik.marksedit.component.MarksEditComponent
import ru.sulgik.ui.component.LocalNestedChildrenStackAnimator
import ru.sulgik.ui.component.NamedConfig
import ru.sulgik.ui.component.TrackedContent

class MarksHostComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = authChildStack(
        source = navigation,
        initialConfiguration = Config.MarksList,
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private fun onMarksEdit(period: DatePeriod, periodTitle: String, title: String) {
        navigation.bringToFront(Config.MarksEdit(period.toParcelable(), periodTitle, title))
    }

    private fun onBack() {
        navigation.pop()
    }

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseComponentContext {
        return when (config) {
            is Config.MarksList -> MarksComponent(
                componentContext = componentContext,
                onEdit = this::onMarksEdit,
            )

            is Config.MarksEdit -> MarksEditComponent(
                period = config.period.toDatePeriod(),
                periodTitle = config.periodTitle,
                title = config.title,
                componentContext = componentContext,
                isBackAvailable = true,
                onBack = this::onBack,
            )
        }
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
        object MarksList : Config {
            override val screenName: String
                get() = "marks"
        }

        @Parcelize
        data class MarksEdit(
            val period: DatePeriodParcelable,
            val periodTitle: String,
            val title: String
        ) : Config {
            override val screenName: String
                get() = "marks_edit"
        }


    }

}