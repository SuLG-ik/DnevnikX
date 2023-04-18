package ru.sulgik.schedule.host.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.authChildStack
import ru.sulgik.schedule.add.component.ScheduleClassesEditComponent
import ru.sulgik.schedule.component.ScheduleListHostComponent
import ru.sulgik.ui.component.LocalNestedChildrenStackAnimator
import ru.sulgik.ui.component.NamedConfig
import ru.sulgik.ui.component.TrackedContent

class ScheduleHostComponent(
    componentContext: AuthorizedComponentContext,
    private val backAvailable: Boolean = false,
    private val onBack: () -> Unit = {},
) : BaseAuthorizedComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = authChildStack(
        source = navigation,
        initialConfiguration = Config.List,
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private fun onEdit() {
        navigation.bringToFront(Config.Edit)
    }


    private fun onBack() {
        navigation.pop { if (!it) onBack.invoke() }
    }

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseComponentContext {
        return when (config) {
            Config.List -> ScheduleListHostComponent(
                componentContext = componentContext,
                backAvailable = backAvailable,
                onBack = this::onBack,
                onSelectClass = this::onEdit,
            )

            Config.Edit -> ScheduleClassesEditComponent(
                componentContext,
                backAvailable = backAvailable,
                onBack = this::onBack,
            )
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val animator = LocalNestedChildrenStackAnimator.current
        childStack.TrackedContent(
            modifier = modifier,
            animation = stackAnimation { _, _, _ -> animator },
        )
    }

    private sealed interface Config : NamedConfig {

        @Parcelize
        object List : Config {
            override val screenName: String
                get() = "schedule_list"
        }

        @Parcelize
        object Edit : Config {
            override val screenName: String
                get() = "schedule_classes_edit"
        }


    }

}