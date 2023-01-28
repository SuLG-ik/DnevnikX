package ru.sulgik.dnevnikx.ui.view

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun animatePulseScale(
    isScaling: Boolean,
    initialScale: Float = 1f,
    targetScale: Float = 0.85f,
): Float {
    var scaleFactor by remember { mutableStateOf(initialScale) }
    LaunchedEffect(key1 = isScaling, block = {
        if (isScaling)
            animate(
                initialValue = initialScale,
                targetValue = targetScale,
                animationSpec = infiniteRepeatable(tween(500), repeatMode = RepeatMode.Reverse),
                block = { value, _ -> scaleFactor = value })
        else
            animate(
                initialValue = scaleFactor,
                targetValue = initialScale,
                animationSpec = spring(),
                block = { value, _ -> scaleFactor = value })
    })
    return scaleFactor
}

fun Modifier.pulse(
    isScaling: Boolean = true,
    initialScale: Float = 1f,
    targetScale: Float = 0.85f,
): Modifier {
    return composed(
        inspectorInfo = {
            name = "pulse"
            properties["initialScale"] = initialScale
            properties["targetScale"] = targetScale
        }
    ) {
        val scaleFactor = animatePulseScale(isScaling, initialScale, targetScale)
        Modifier.graphicsLayer(scaleX= scaleFactor, scaleY = scaleFactor)
    }

}