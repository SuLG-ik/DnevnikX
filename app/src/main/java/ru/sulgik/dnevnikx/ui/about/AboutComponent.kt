package ru.sulgik.dnevnikx.ui.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.core.component.get
import ru.sulgik.dnevnikx.BuildConfig
import ru.sulgik.dnevnikx.data.AboutData
import ru.sulgik.dnevnikx.platform.UriHandler
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.DIComponentContext

class AboutComponent(componentContext: DIComponentContext) :
    BaseComponentContext(componentContext) {


    private val state = AboutData(
        application = AboutData.ApplicationData(
            name = "DnevnikX",
            version = BuildConfig.APP_VERSION,
        ),
        developer = AboutData.DeveloperData(
            name = "@vollllodya",
            uri = "https://t.me/vollllodya",
        ),
        domain = AboutData.DomainInfo(
            name = "Новосибирская область",
            domain = "school.nso.ru",
            uri = "https://school.nso.ru"
        )
    )

    private val uriHandler = get<UriHandler>()

    private fun onDeveloper() {
        uriHandler.open(state.developer.uri)
    }

    private fun onDomain() {
        uriHandler.open(state.domain.uri)
    }

    @Composable
    override fun Content(modifier: Modifier) {
        AboutScreen(
            aboutData = state,
            onDeveloper = this::onDeveloper,
            onDomain = this::onDomain,
            modifier = modifier
        )
    }

}