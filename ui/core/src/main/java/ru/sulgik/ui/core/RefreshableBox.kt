package ru.sulgik.ui.core

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshableBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    vibrationEnabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val refreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = onRefresh)
    val progress = refreshState.progress
    if (vibrationEnabled) {
        val vibration = LocalHapticFeedback.current
        val vibrated = remember { mutableStateOf(false) }
        SideEffect {
            when {
                progress >= 1f && !vibrated.value -> {
                    vibration.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    vibrated.value = true
                }

                progress < 1f ->
                    vibrated.value = false
            }
        }
    }
    Box(
        modifier = modifier
            .pullRefresh(
                state = refreshState,
                enabled = enabled,
            ),
    ) {
        DesignedIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
        val columnOffset by animateDpAsState(
            targetValue = getRefreshOffset(
                refreshing = refreshing,
                progress = progress
            ), label = "columnOffset"
        )
        Box(
            modifier = Modifier.padding(top = columnOffset),
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DesignedIndicator(refreshing: Boolean, refreshState: PullRefreshState, modifier: Modifier) {
    val progress = minOf(1.4f, refreshState.progress)
    val indicatorOffset by animateDpAsState(
        targetValue = getRefreshProgressOffset(
            refreshing = refreshing,
            progress = progress
        ),
        label = "indicatorOffset"
    )
    val indicatorAlpha by
    animateFloatAsState(
        targetValue = getIndicatorAlpha(
            refreshing = refreshing,
            progress = FastOutSlowInEasing.transform(progress)
        ),
        label = "indicatorAlpha",
    )
    val scale by animateFloatAsState(
        targetValue = getRefreshProgressScale(
            refreshing = refreshing,
            progress = progress
        ), label = "indicatorScale"
    )
    if (refreshing) {
        CircularProgressIndicator(
            modifier = modifier
                .graphicsLayer(translationY = with(
                    LocalDensity.current
                ) { indicatorOffset.toPx() })
        )
    } else {
        CircularProgressIndicator(
            progress = progress,
            modifier = modifier
                .graphicsLayer(scaleX = scale,
                    scaleY = scale,
                    alpha = indicatorAlpha,
                    translationY = with(
                        LocalDensity.current
                    ) { indicatorOffset.toPx() })
        )
    }
}

fun getRefreshOffset(refreshing: Boolean, progress: Float): Dp {
    if (progress == 0f && refreshing) {
        return 60.dp
    }
    return 70.dp * progress
}


private fun getRefreshProgressOffset(refreshing: Boolean, progress: Float): Dp {
    if (progress == 0f && refreshing) {
        return 5.dp
    }
    return (40.dp * progress) - 30.dp
}

private fun getRefreshProgressScale(refreshing: Boolean, progress: Float): Float {
    if (progress == 0f && refreshing) {
        return 1f
    }
    return (0.75f * progress).coerceIn(0.75f, 1f)
}


private fun getIndicatorAlpha(refreshing: Boolean, progress: Float): Float {
    if (progress == 0f && refreshing) {
        return 1f
    }
    return (progress).coerceIn(0f, 1f)
}
