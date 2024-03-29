package ru.sulgik.application.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.account.host.component.AccountHostComponent
import ru.sulgik.account.selector.component.AccountSelectorComponent
import ru.sulgik.application.ui.ApplicationScreen
import ru.sulgik.application.ui.Gender
import ru.sulgik.application.ui.NavigationConfig
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.core.AuthorizedComponentContext
import ru.sulgik.core.BaseAuthorizedComponentContext
import ru.sulgik.core.authChildStack
import ru.sulgik.core.childAuthContext
import ru.sulgik.core.states
import ru.sulgik.diary.component.DiaryComponent
import ru.sulgik.marks.host.component.MarksHostComponent
import ru.sulgik.modal.ui.FloatingModalUI
import ru.sulgik.ui.component.NamedConfig
import ru.sulgik.ui.component.TrackedContent

private fun AccountSelectorComponent.SelectedAccount.Gender.toState(): Gender {
    return when (this) {
        AccountSelectorComponent.SelectedAccount.Gender.MALE -> Gender.MALE
        AccountSelectorComponent.SelectedAccount.Gender.FEMALE -> Gender.FEMALE
    }
}

class ApplicationComponent(
    componentContext: AuthorizedComponentContext,
    onReAuth: (AuthScope) -> Unit,
    onAddAccount: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack by authChildStack(
        source = navigation,
        initialConfiguration = Config.Dairy,
        handleBackButton = true,
        childFactory = this::createChild,
    ).states(this)

    private val accountSelector =
        AccountSelectorComponent(
            componentContext = childAuthContext(key = "account_selector"),
            onAccountSelected = onReAuth,
            onAddAccount = onAddAccount,
        )

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseAuthorizedComponentContext {
        return when (config) {
            Config.Dairy -> DiaryComponent(componentContext)
            Config.Marks -> MarksHostComponent(componentContext)
            Config.Profile -> AccountHostComponent(
                componentContext = componentContext,
                onSelectAccount = this::onExpandAccountSelector
            )
        }
    }

    private fun onNavigate(config: NavigationConfig) {
        navigation.bringToFront(config.toState())
    }

    private fun NavigationConfig.toState(): Config {
        return when (this) {
            NavigationConfig.Dairy -> Config.Dairy
            NavigationConfig.Marks -> Config.Marks
            NavigationConfig.Profile -> Config.Profile
        }
    }

    private fun Config.toState(): NavigationConfig {
        return when (this) {
            Config.Dairy -> NavigationConfig.Dairy
            Config.Marks -> NavigationConfig.Marks
            Config.Profile -> NavigationConfig.Profile
        }
    }

    private fun onSecondaryNavigate(config: NavigationConfig) {
        when (config.toState()) {
            is Config.Profile -> onExpandAccountSelector()
            else -> {}
        }
    }

    private fun onExpandAccountSelector() {
        accountSelector.onAccountSelection()
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val state = childStack
        FloatingModalUI(component = accountSelector) {
            ApplicationScreen(
                gender = accountSelector.selectedAccount?.gender?.toState(),
                currentNavigation = state.active.configuration.toState(),
                onNavigate = this::onNavigate,
                onSecondaryNavigate = this::onSecondaryNavigate,
                modifier = modifier,
            ) {
                state.TrackedContent(
                    modifier = Modifier.fillMaxSize(),
                    animation = null,
                )
            }
        }
    }

    sealed interface Config : NamedConfig {

        @Parcelize
        object Dairy : Config {
            override val screenName: String
                get() = "diary"
        }

        @Parcelize
        object Marks : Config {
            override val screenName: String?
                get() = null
        }

        @Parcelize
        object Profile : Config {
            override val screenName: String?
                get() = null
        }


    }
}