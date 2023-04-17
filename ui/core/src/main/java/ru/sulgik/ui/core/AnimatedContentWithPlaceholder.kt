package ru.sulgik.ui.core

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


val LocalAnimatedContentTransition =
    staticCompositionLocalOf { fadeIn(tween(300)) with fadeOut(tween(250)) }

@Composable
fun <T : Any> AnimatedContentWithPlaceholder(
    isLoading: Boolean,
    state: T?,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    label: String = "AnimatedContent",
    placeholderContent: @Composable() AnimatedVisibilityScope.() -> Unit = {},
    noDataContent: @Composable() AnimatedVisibilityScope.() -> Unit = {},
    content: @Composable() AnimatedVisibilityScope.(T) -> Unit,
) {
    val transition = LocalAnimatedContentTransition.current
    AnimatedContent(
        targetState = isLoading,
        transitionSpec = { transition },
        contentAlignment = contentAlignment,
        label = label,
        modifier = modifier,
    ) { it ->
        if (it) {
            placeholderContent()
        } else {
            if (state == null) {
                noDataContent()
            } else {
                content(state)
            }
        }
    }
}