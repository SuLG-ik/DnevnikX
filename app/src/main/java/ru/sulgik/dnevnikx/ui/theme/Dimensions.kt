package ru.sulgik.dnevnikx.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import ru.sulgik.ui.core.ExtendedDimensions
import ru.sulgik.ui.core.ExtendedTheme

@Composable
fun DnevnikXExtendedTheme(content: @Composable () -> Unit) {
    ExtendedTheme(dimensions = DefaultDimensions, content)
}

@Suppress("PrivatePropertyName")
private val DefaultDimensions = ExtendedDimensions(
    mainContentPadding = 10.dp,
    contentSpaceBetween = 10.dp,
    leadingIconSizeLarge = 35.dp,
)