package ru.sulgik.about.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.core.component.get
import ru.sulgik.about.mvi.AboutStore
import ru.sulgik.about.ui.AboutScreen
import ru.sulgik.common.platform.UriHandler
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.core.getStore
import ru.sulgik.core.states

class AboutComponent(
    componentContext: DIComponentContext,
    val isBackAvailable: Boolean = false,
    val onBack: () -> Unit = {},
) :
    BaseComponentContext(componentContext) {


    private val store: AboutStore = getStore()

    private val state by store.states(this)

    private val uriHandler = get<UriHandler>()

    private fun onDeveloper() {
        state.data?.developer?.let { uriHandler.open(it.uri) }
    }

    private fun onDomain() {
        state.data?.domain?.let { uriHandler.open(it.uri) }
    }

    private fun onBack() {
        if (isBackAvailable) {
            onBack.invoke()
        }
    }

    @Composable
    override fun Content(modifier: Modifier) {
        val aboutData = state.data
        if (aboutData != null)
            AboutScreen(
                aboutData = aboutData,
                backAvailable = isBackAvailable,
                onDeveloper = this::onDeveloper,
                onDomain = this::onDomain,
                onBack = this::onBack,
                modifier = modifier
            )
    }

}