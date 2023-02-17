package ru.sulgik.dnevnikx.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sulgik.dnevnikx.ui.AuthorizedComponentContext
import ru.sulgik.dnevnikx.ui.BaseAuthorizedComponentContext

class ProfileComponent(
    componentContext: AuthorizedComponentContext,
    private val onSchedule: () -> Unit,
    private val onUpdates: () -> Unit,
    private val onAbout: () -> Unit,
    private val onSelectAccount: () -> Unit,
) : BaseAuthorizedComponentContext(componentContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        ProfileScreen(
            onSchedule = onSchedule,
            onUpdates = onUpdates,
            onAbout = onAbout,
            onSelectAccount = onSelectAccount,
            modifier = modifier,
        )
    }


}