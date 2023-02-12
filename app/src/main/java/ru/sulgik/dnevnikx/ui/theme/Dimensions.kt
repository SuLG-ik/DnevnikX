package ru.sulgik.dnevnikx.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


class Dimensions private constructor(
    val defaultPadding: Dp = 15.dp,
) {

    companion object {

        val defaultPadding @Composable get() = LocalDimensions.current.defaultPadding

        private val LocalDimensions = compositionLocalOf { Dimensions() }

    }

}