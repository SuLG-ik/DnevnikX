package ru.sulgik.application.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.sulgik.ui.core.BottomAppBar
import ru.sulgik.ui.core.CombinedNavigationBarItem
import ru.sulgik.ui.core.outlined


sealed interface NavigationConfig {

    val icon: Int
    val title: Int
    val haptic: Boolean


    object Dairy : NavigationConfig {
        override val icon: Int
            get() = R.drawable.nav_dairy_icon
        override val title: Int
            get() = R.string.nav_diary_title
        override val haptic: Boolean
            get() = false
    }

    object Marks : NavigationConfig {
        override val icon: Int
            get() = R.drawable.nav_marks_icon
        override val title: Int
            get() = R.string.nav_marks_title
        override val haptic: Boolean
            get() = false

    }

    object Profile : NavigationConfig {
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

@Composable
fun ApplicationBottomNavigation(
    navItems: List<NavigationConfig>,
    active: NavigationConfig,
    onClick: (NavigationConfig) -> Unit,
    onLongClick: (NavigationConfig) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
            .outlined(shape = MaterialTheme.shapes.large),
    ) {
        navItems.forEach {
            CombinedNavigationBarItem(
                selected = it == active,
                onClick = { onClick(it) },
                onLongClick = { onLongClick(it) },
                haptic = it.haptic,
                icon = {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(stringResource(id = it.title))
                })
        }
    }
}

@Composable
fun ApplicationScreen(
    currentNavigation: NavigationConfig,
    onNavigate: (NavigationConfig) -> Unit,
    onSecondaryNavigate: (NavigationConfig) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        bottomBar = {
            ApplicationBottomNavigation(
                navItems = NavigationConfig.navItems,
                active = currentNavigation,
                onClick = onNavigate,
                onLongClick = onSecondaryNavigate,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .padding(it),
            content = content,
        )
    }
}