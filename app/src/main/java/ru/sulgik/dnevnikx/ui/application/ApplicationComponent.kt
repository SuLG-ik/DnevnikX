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
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.Content
import ru.sulgik.dnevnikx.ui.authChildStack
import ru.sulgik.dnevnikx.ui.diary.DiaryComponent
import ru.sulgik.dnevnikx.ui.marks.MarksComponent
import ru.sulgik.dnevnikx.ui.profile.ProfileComponent

class ApplicationComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    private val navigation = StackNavigation<Config>()

    private val childStack = authChildStack(
        source = navigation,
        initialConfiguration = Config.Dairy,
        handleBackButton = true,
        childFactory = this::createChild,
    )

    private fun createChild(
        config: Config,
        componentContext: AuthorizedComponentContext,
    ): BaseAuthorizedComponentContext {
        return when (config) {
            Config.Dairy -> DiaryComponent(componentContext)
            Config.Marks -> MarksComponent(componentContext)
            Config.Profile -> ProfileComponent(componentContext)
        }
    }

    private fun onNavigate(config: Config) {
        navigation.bringToFront(config)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalDecomposeApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val childState by childStack.subscribeAsState()
        Scaffold(
            bottomBar = {
                ApplicationBottomNavigation(
                    childState.active.configuration,
                    onClick = this::onNavigate,
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

    sealed interface Config : Parcelable {

        val icon: Int
        val title: Int


        @Parcelize
        data object Dairy : Config {
            override val icon: Int
                get() = R.drawable.nav_dairy_icon
            override val title: Int
                get() = R.string.nav_diary_title
        }

        @Parcelize
        data object Marks : Config {
            override val icon: Int
                get() = R.drawable.nav_marks_icon
            override val title: Int
                get() = R.string.nav_marks_title

        }

        @Parcelize
        data object Profile : Config {
            override val icon: Int
                get() = R.drawable.nav_profile_icon
            override val title: Int
                get() = R.string.nav_profile_title

        }

        companion object {
            val navItems by lazy(LazyThreadSafetyMode.NONE) { listOf(Dairy, Marks, Profile) }
        }

    }

}