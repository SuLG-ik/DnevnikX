package ru.sulgik.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

@Composable
fun ExtendedTheme(dimensions: ExtendedDimensions, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        values = arrayOf(LocalExtendedDimensions provides dimensions),
        content = content,
    )
}

object ExtendedTheme {

    val dimensions: ExtendedDimensions
        @Composable
        get() = LocalExtendedDimensions.current

}

data class ExtendedDimensions(
    val mainContentPadding: Dp,
    val contentSpaceBetween: Dp,
    val leadingIconSizeLarge: Dp,
)

private val LocalExtendedDimensions =
    compositionLocalOf<ExtendedDimensions> { error("ExtendedTheme does not provided") }

