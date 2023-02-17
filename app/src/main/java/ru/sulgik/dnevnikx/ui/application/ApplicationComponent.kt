package ru.sulgik.dnevnikx.ui.application

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.data.Account
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.FloatingModalUI
import ru.sulgik.dnevnikx.ui.ModalUI
import ru.sulgik.dnevnikx.ui.authChildStack
import ru.sulgik.dnevnikx.ui.childAuthContext
import ru.sulgik.dnevnikx.ui.childDIContext
import ru.sulgik.dnevnikx.ui.diary.DiaryComponent
import ru.sulgik.dnevnikx.ui.marks.MarksComponent
import ru.sulgik.dnevnikx.ui.profile.ProfileHostComponent
import ru.sulgik.dnevnikx.ui.profile.selector.AccountSelectorComponent

class ApplicationComponent(
    componentContext: AuthorizedComponentContext,
    onReAuth: (Account) -> Unit,
    onAddAccount: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = authChildStack(
        source = navigation,
        initialConfiguration = Config.Dairy,
        handleBackButton = true,
        childFactory = this::createChild,
    )

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
            Config.Marks -> MarksComponent(componentContext)
            Config.Profile -> ProfileHostComponent(
                componentContext = componentContext,
                onSelectAccount = this::onExpandAccountSelector
            )
        }
    }

    private fun onNavigate(config: Config) {
        navigation.bringToFront(config)
    }

    private fun onSecondaryNavigate(config: Config) {
        when (config) {
            is Config.Profile -> onExpandAccountSelector()
            else -> {}
        }
    }

    private fun onExpandAccountSelector() {
        accountSelector.onAccountSelection()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(modifier: Modifier) {
        FloatingModalUI(component = accountSelector) {
            val childState by childStack.subscribeAsState()
            Scaffold(
                bottomBar = {
                    ApplicationBottomNavigation(
                        Config.navItems,
                        childState.active.configuration,
                        onClick = this::onNavigate,
                        onLongClick = this::onSecondaryNavigate,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize()
                ) {
                    childState.Content(
                        Modifier.fillMaxSize(),
                        animation = null,
                    )
                }
            }
        }
    }

    sealed interface Config : Parcelable {

        val icon: Int
        val title: Int
        val haptic: Boolean


        @Parcelize
        object Dairy : Config {
            override val icon: Int
                get() = R.drawable.nav_dairy_icon
            override val title: Int
                get() = R.string.nav_diary_title
            override val haptic: Boolean
                get() = false
        }

        @Parcelize
        object Marks : Config {
            override val icon: Int
                get() = R.drawable.nav_marks_icon
            override val title: Int
                get() = R.string.nav_marks_title
            override val haptic: Boolean
                get() = false

        }

        @Parcelize
        object Profile : Config {
            override val icon: Int
                get() = R.drawable.nav_profile_icon
            override val title: Int
                get() = R.string.nav_profile_title
            override val haptic: Boolean
                get() = true

        }

        companion object {
            val navItems by lazy(LazyThreadSafetyMode.NONE) { listOf(Dairy, Marks, Profile) }
        }

    }

}