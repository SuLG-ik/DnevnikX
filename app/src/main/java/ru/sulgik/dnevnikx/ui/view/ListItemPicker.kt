package ru.sulgik.dnevnikx.ui.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float,
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}

@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    dividersColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
    marked: (T) -> Boolean = { false },
) {
    val minimumAlpha = 0.3f
    val minimumScale = 0.75f
    val verticalMargin = 8.dp
    val numbersColumnHeight = 80.dp
    val halfNumbersColumnHeight = numbersColumnHeight / 2
    val halfNumbersColumnHeightPx = with(LocalDensity.current) { halfNumbersColumnHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            SideEffect {
                val index = list.indexOf(value)
                val offsetRange =
                    -((list.count() - 1) - index) * halfNumbersColumnHeightPx to
                            index * halfNumbersColumnHeightPx

                updateBounds(offsetRange.first, offsetRange.second)
            }
        }

    val coercedAnimatedOffset = animatedOffset.value % halfNumbersColumnHeightPx

    val indexOfElement =
        getItemIndexForOffset(list, value, animatedOffset.value, halfNumbersColumnHeightPx)

    var dividersWidth by remember { mutableStateOf(0.dp) }
    val dividersHeight = with(LocalDensity.current) {
        textStyle.fontSize.toDp() + 10.dp
    }

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnHeightPx
                                val coercedAnchors =
                                    listOf(
                                        -halfNumbersColumnHeightPx,
                                        0f,
                                        halfNumbersColumnHeightPx
                                    )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    halfNumbersColumnHeightPx * (target / halfNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val result = list.elementAt(
                            getItemIndexForOffset(list, value, endValue, halfNumbersColumnHeightPx)
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .padding(vertical = numbersColumnHeight / 3 + verticalMargin * 2),
        content = {
            Box {
                Box(
                    modifier
                        .align(Alignment.Center)
                        .width(dividersWidth)
                        .height(dividersHeight)
                        .clip(MaterialTheme.shapes.large)
                        .background(color = dividersColor)
                )
                Box(
                    modifier = Modifier
                        .padding(vertical = verticalMargin, horizontal = 20.dp)
                        .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
                ) {
                    val baseLabelModifier = Modifier.align(Alignment.Center)
                    ProvideTextStyle(textStyle) {
                        if (indexOfElement > 2) {
                            val item = list.elementAt(indexOfElement - 3)
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = -halfNumbersColumnHeight * 3)
                                    .graphicsLayer(
                                        scaleX = minimumScale,
                                        scaleY = minimumScale,
                                        alpha = minimumAlpha
                                    )
                            )
                        }

                        if (indexOfElement > 1) {
                            val item = list.elementAt(indexOfElement - 2)
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = -halfNumbersColumnHeight * 2)
                                    .graphicsLayer(
                                        scaleX = minimumScale,
                                        scaleY = minimumScale,
                                        alpha = minimumAlpha
                                    )
                            )
                        }

                        if (indexOfElement > 0) {
                            val item = list.elementAt(indexOfElement - 1)
                            val scale = maxOf(
                                minimumScale,
                                coercedAnimatedOffset / halfNumbersColumnHeightPx
                            )
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = -halfNumbersColumnHeight)
                                    .graphicsLayer(
                                        scaleX = scale, scaleY = scale, alpha = (
                                                maxOf(
                                                    minimumAlpha,
                                                    coercedAnimatedOffset / halfNumbersColumnHeightPx
                                                )
                                                )
                                    )
                            )
                        }
                        val mainItem = list.elementAt(indexOfElement)
                        val scale = maxOf(
                            minimumScale,
                            1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                        )
                        Label(
                            text = label(mainItem),
                            marked = marked(mainItem),
                            modifier = baseLabelModifier
                                .graphicsLayer(
                                    scaleX = scale, scaleY = scale, alpha = (maxOf(
                                        minimumAlpha,
                                        1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                                    ))
                                )
                        )
                        if (indexOfElement < list.count() - 1) {
                            val item = list.elementAt(indexOfElement + 1)
                            val scale = maxOf(
                                minimumScale,
                                -coercedAnimatedOffset / halfNumbersColumnHeightPx
                            )
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = halfNumbersColumnHeight)
                                    .graphicsLayer(
                                        scaleX = scale, scaleY = scale, alpha = maxOf(
                                            minimumAlpha,
                                            -coercedAnimatedOffset / halfNumbersColumnHeightPx
                                        )
                                    )
                            )
                        }

                        if (indexOfElement < list.count() - 2) {
                            val item = list.elementAt(indexOfElement + 2)
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = halfNumbersColumnHeight * 2)
                                    .graphicsLayer(
                                        scaleX = minimumScale,
                                        scaleY = minimumScale,
                                        alpha = minimumAlpha
                                    )
                            )
                        }

                        if (indexOfElement < list.count() - 3) {
                            val item = list.elementAt(indexOfElement + 3)
                            Label(
                                text = label(item),
                                marked = marked(item),
                                modifier = baseLabelModifier
                                    .offset(y = halfNumbersColumnHeight * 3)
                                    .graphicsLayer(
                                        scaleX = minimumScale,
                                        scaleY = minimumScale,
                                        alpha = minimumAlpha
                                    )
                            )
                        }
                    }
                }
            }

        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        dividersWidth = placeables
            .first()
            .width
            .toDp()

        // Set the size of the layout as big as it can
        layout(dividersWidth.toPx().toInt(), placeables
            .sumOf {
                it.height
            }
        ) {
            // Track the y co-ord we have placed children up to
            var yPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = 0, y = yPosition)

                // Record the y co-ord placed up to
                yPosition += placeable.height
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Label(text: String, marked: Boolean, modifier: Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = if (marked) MaterialTheme.colorScheme.primary else Color.Unspecified,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                // FIXME: Empty to disable text selection
            })
        },
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}