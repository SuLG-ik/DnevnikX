package ru.sulgik.dnevnikx.ui.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.ui.view.outlined

@Composable
fun ApplicationBottomNavigation(
    active: ApplicationComponent.Config,
    onClick: (ApplicationComponent.Config) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
            .outlined(),
    ) {
        ApplicationComponent.Config.navItems.forEach {
            NavigationBarItem(selected = it == active, onClick = { onClick(it) }, icon = {
                Icon(
                    painter = painterResource(id = it.icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }, label = {
                Text(stringResource(id = it.title))
            })
        }
    }
}