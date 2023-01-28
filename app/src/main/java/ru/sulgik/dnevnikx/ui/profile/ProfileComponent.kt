package ru.sulgik.dnevnikx.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext

class ProfileComponent(
    componentContext: AuthorizedComponentContext,
) : BaseAuthorizedComponentContext(componentContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Профиль")
        }
    }

}