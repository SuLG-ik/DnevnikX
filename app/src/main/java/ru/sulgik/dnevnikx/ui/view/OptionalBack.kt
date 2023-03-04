package ru.sulgik.dnevnikx.ui.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

fun optionalBackNavigationIcon(
    backAvailable: Boolean,
    onBack: () -> Unit,
): @Composable (() -> Unit) {
    if (!backAvailable) return {}
    return {
        IconButton(onClick = onBack) {
            Icon(Icons.Outlined.ArrowBack, contentDescription = "назад")
        }
    }
}