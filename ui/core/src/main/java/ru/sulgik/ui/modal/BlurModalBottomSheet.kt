package ru.sulgik.ui.modal

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ru.sulgik.ui.modal.ModalSheetValue.*
import ru.sulgik.ui.modal.SwipeableV2Defaults.PositionalThreshold
import ru.sulgik.ui.modal.SwipeableV2Defaults.VelocityThreshold
import java.lang.Float.max
import kotlin.math.roundToInt


/**
 * Possible values of [ModalSheetState].
 */
enum class ModalSheetValue {
    /**
     * The bottom sheet is not visible.
     */
    Hidden,

    /**
     * The bottom sheet is visible at full height.
     */
    Expanded,

    /**
     * The bottom sheet is partially visible at 50% of the screen height. This state is only
     * enabled if the height of the bottom sheet is more than 50% of the screen height.
     */
    HalfExpanded
}

/**
 * State of the [ModalSheetLayout] composable.
 *
 * @param initialValue The initial value of the state. <b>Must not be set to
 * [ModalSheetValue.HalfExpanded] if [isSkipHalfExpanded] is set to true.</b>
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param isSkipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [ModalSheetValue.HalfExpanded].</b>
 * If supplied with [ModalSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 */
@OptIn(ExperimentalMaterialApi::class)
open class ModalSheetState(
    initialValue: ModalSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal val isSkipHalfExpanded: Boolean,
    confirmValueChange: (ModalSheetValue) -> Boolean = { true },
) {
    /**
     * Whether the bottom sheet is visible.
     */
    internal val swipeableState = SwipeableV2State(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        positionalThreshold = PositionalThreshold,
        velocityThreshold = VelocityThreshold
    )


    val currentValue: ModalSheetValue
        get() = swipeableState.currentValue


    val targetValue: ModalSheetValue
        get() = swipeableState.targetValue

    /**
     * Whether the bottom sheet is visible.
     */

    val isVisible: Boolean
        get() = swipeableState.currentValue != Hidden


    internal val hasHalfExpandedState: Boolean
        get() = swipeableState.hasAnchorForValue(HalfExpanded)

    @Deprecated(
        message = "This constructor is deprecated. confirmStateChange has been renamed to " +
                "confirmValueChange.",
        replaceWith = ReplaceWith(
            "ModalBottomSheetState(" +
                    "initialValue, animationSpec, confirmStateChange, false)"
        )
    )
    @Suppress("Deprecation")
    constructor(
        initialValue: ModalSheetValue,
        animationSpec: AnimationSpec<Float>,
        confirmStateChange: (ModalSheetValue) -> Boolean
    ) : this(initialValue, animationSpec, isSkipHalfExpanded = false, confirmStateChange)

    init {
        if (isSkipHalfExpanded) {
            require(initialValue != HalfExpanded) {
                "The initial value must not be set to HalfExpanded if skipHalfExpanded is set to" +
                        " true."
            }
        }
    }

    /**
     * Show the bottom sheet with animation and suspend until it's shown. If the sheet is taller
     * than 50% of the parent's height, the bottom sheet will be half expanded. Otherwise it will be
     * fully expanded.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun show() {
        val targetValue = when {
            hasHalfExpandedState -> HalfExpanded
            else -> Expanded
        }
        animateTo(targetValue)
    }

    /**
     * Half expand the bottom sheet if half expand is enabled with animation and suspend until it
     * animation is complete or cancelled
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun halfExpand() {
        if (!hasHalfExpandedState) {
            return
        }
        animateTo(HalfExpanded)
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it if fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */

    internal suspend fun expand() {
        if (!swipeableState.hasAnchorForValue(Expanded)) {
            return
        }
        animateTo(Expanded)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it if fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() = animateTo(Hidden)

    internal suspend fun animateTo(
        target: ModalSheetValue,
        velocity: Float = swipeableState.lastVelocity
    ) = swipeableState.animateTo(target, velocity)

    internal suspend fun snapTo(target: ModalSheetValue) = swipeableState.snapTo(target)

    internal fun trySnapTo(target: ModalSheetValue): Boolean {
        return swipeableState.trySnapTo(target)
    }

    internal fun requireOffset() = swipeableState.requireOffset()

    internal val lastVelocity: Float get() = swipeableState.lastVelocity

    internal val isAnimationRunning: Boolean get() = swipeableState.isAnimationRunning

    companion object {
        /**
         * The default [Saver] implementation for [ModalBottomSheetState].
         * Saves the [currentValue] and recreates a [ModalBottomSheetState] with the saved value as
         * initial value.
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChange: (ModalSheetValue) -> Boolean,
            skipHalfExpanded: Boolean,
        ): Saver<ModalSheetState, *> = Saver(
            save = { it.currentValue },
            restore = {
                ModalSheetState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    isSkipHalfExpanded = skipHalfExpanded,
                    confirmValueChange = confirmValueChange
                )
            }
        )

    }
}

/**
 * Create a [ModalSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberModalSheetState(
    initialValue: ModalSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmValueChange: (ModalSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = false,
): ModalSheetState {
    // Key the rememberSaveable against the initial value. If it changed we don't want to attempt
    // to restore as the restored value could have been saved with a now invalid set of anchors.
    // b/152014032
    return key(initialValue) {
        rememberSaveable(
            initialValue, animationSpec, skipHalfExpanded, confirmValueChange,
            saver = ModalSheetState.Saver(
                animationSpec = animationSpec,
                skipHalfExpanded = skipHalfExpanded,
                confirmValueChange = confirmValueChange
            )
        ) {
            ModalSheetState(
                initialValue = initialValue,
                animationSpec = animationSpec,
                isSkipHalfExpanded = skipHalfExpanded,
                confirmValueChange = confirmValueChange
            )
        }
    }
}


/**
 * <a href="https://material.io/components/sheets-bottom#modal-bottom-sheet" class="external" target="_blank">Material Design modal bottom sheet</a>.
 *
 * Modal bottom sheets present a set of choices while blocking interaction with the rest of the
 * screen. They are an alternative to inline menus and simple dialogs, providing
 * additional room for content, iconography, and actions.
 *
 * ![Modal bottom sheet image](https://developer.android.com/images/reference/androidx/compose/material/modal-bottom-sheet.png)
 *
 * A simple example of a modal bottom sheet looks like this:
 *
 * @sample androidx.compose.material.samples.ModalBottomSheetSample
 *
 * @param sheetContent The content of the bottom sheet.
 * @param modifier Optional [Modifier] for the entire component.
 * @param sheetState The state of the bottom sheet.
 * @param sheetShape The shape of the bottom sheet.
 * @param sheetElevation The elevation of the bottom sheet.
 * @param sheetBackgroundColor The background color of the bottom sheet.
 * @param sheetContentColor The preferred content color provided by the bottom sheet to its
 * children. Defaults to the matching content color for [sheetBackgroundColor], or if that is not
 * a color from the theme, this will keep the same content color set above the bottom sheet.
 * @param scrimColor The color of the scrim that is applied to the rest of the screen when the
 * bottom sheet is visible. If the color passed is [Color.Unspecified], then a scrim will no
 * longer be applied and the bottom sheet will not block interaction with the rest of the screen
 * when visible.
 * @param content The content of rest of the screen.
 */
@Composable
fun ModalSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    blur: Dp = 16.dp,
    scale: Float = 0.98f,
    sheetState: ModalSheetState =
        rememberModalSheetState(Hidden),
    direction: ModalSheetDirection = ModalSheetDirection.BOTTOM,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalSheetDefaults.Elevation,
    sheetPadding: Dp = 10.dp,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalSheetDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val orientation = Orientation.Vertical
    val anchorChangeHandler = remember(sheetState, scope) {
        ModalBottomSheetAnchorChangeHandler(
            state = sheetState,
            animateTo = { target, velocity ->
                scope.launch { sheetState.animateTo(target, velocity = velocity) }
            },
            snapTo = { target ->
                val didSnapSynchronously = sheetState.trySnapTo(target)
                if (!didSnapSynchronously) scope.launch { sheetState.snapTo(target) }
            }
        )
    }
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val scaleFactor =
            animateFloatAsState(
                targetValue = if (sheetState.targetValue != Hidden) scale else 1f,
                animationSpec = tween(250),
                label = "bottom_sheet_scale"
            )
        val blurFactor =
            animateDpAsState(
                targetValue = if (sheetState.targetValue != Hidden) blur else 0.dp,
                animationSpec = tween(250),
                label = "bottom_sheet_blur"
            )
        Box(
            Modifier
                .fillMaxSize()
                .blur(blurFactor.value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(scaleX = scaleFactor.value, scaleY = scaleFactor.value)
            ) {
                content()
            }
            Scrim(
                color = scrimColor,
                onDismiss = {
                    if (sheetState.swipeableState.confirmValueChange(Hidden)) {
                        scope.launch { sheetState.hide() }
                    }
                },
                visible = sheetState.targetValue != Hidden
            )
        }
        Surface(
            Modifier
                .align(Alignment.TopCenter) // We offset from the top so we'll center from there
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .nestedScroll(
                    remember(sheetState.swipeableState, orientation) {
                        ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                            state = sheetState.swipeableState,
                            orientation = orientation
                        )
                    }
                )
                .offset {
                    IntOffset(
                        0,
                        sheetState.swipeableState
                            .requireOffset()
                            .roundToInt()
                    )
                }
                .swipeableV2(
                    state = sheetState.swipeableState,
                    orientation = orientation,
                    enabled = sheetState.swipeableState.currentValue != Hidden,
                )
                .swipeAnchors(
                    state = sheetState.swipeableState,
                    possibleValues = setOf(
                        Hidden,
                        HalfExpanded,
                        Expanded
                    ),
                    anchorChangeHandler = anchorChangeHandler
                ) { state, sheetSize ->
                    when (state) {
                        Hidden -> direction.hiddenAnchor(
                            fullHeight = fullHeight,
                            sheetHeight = sheetSize.height,
                        )

                        HalfExpanded -> direction.halfExpandedAnchor(
                            fullHeight = fullHeight,
                            sheetHeight = sheetSize.height,
                            isSkipHalfExpanded = sheetState.isSkipHalfExpanded
                        )

                        Expanded -> direction.expandedAnchor(
                            fullHeight = fullHeight,
                            sheetHeight = sheetSize.height,
                        )
                    }
                }
                .semantics {
                    if (sheetState.isVisible) {
                        dismiss {
                            if (sheetState.swipeableState.confirmValueChange(
                                    Hidden
                                )
                            ) {
                                scope.launch { sheetState.hide() }
                            }
                            true
                        }
                        if (sheetState.swipeableState.currentValue == HalfExpanded) {
                            expand {
                                if (sheetState.swipeableState.confirmValueChange(
                                        Expanded
                                    )
                                ) {
                                    scope.launch { sheetState.expand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) {
                            collapse {
                                if (sheetState.swipeableState.confirmValueChange(
                                        HalfExpanded
                                    )
                                ) {
                                    scope.launch { sheetState.halfExpand() }
                                }
                                true
                            }
                        }
                    }
                }
                .padding(sheetPadding),
            shape = sheetShape,
            shadowElevation = sheetElevation,
            color = sheetBackgroundColor,
            contentColor = sheetContentColor
        ) {
            Column(content = sheetContent)
        }
    }
}

enum class ModalSheetDirection {
    BOTTOM {
        override fun hiddenAnchor(fullHeight: Float, sheetHeight: Int): Float {
            return fullHeight
        }

        override fun halfExpandedAnchor(
            fullHeight: Float,
            sheetHeight: Int,
            isSkipHalfExpanded: Boolean,
        ): Float? {
            return when {
                sheetHeight < fullHeight / 2f -> null
                isSkipHalfExpanded -> null
                else -> fullHeight / 2f
            }
        }

        override fun expandedAnchor(
            fullHeight: Float,
            sheetHeight: Int
        ): Float? {
            return if (sheetHeight != 0) {
                max(0f, fullHeight - sheetHeight)
            } else null
        }
    },
    TOP {
        override fun hiddenAnchor(fullHeight: Float, sheetHeight: Int): Float {
            return 0f - sheetHeight
        }

        override fun halfExpandedAnchor(
            fullHeight: Float,
            sheetHeight: Int,
            isSkipHalfExpanded: Boolean,
        ): Float? {
            return when {
                sheetHeight < fullHeight / 2f -> null
                isSkipHalfExpanded -> null
                else -> fullHeight / 2f
            }
        }

        override fun expandedAnchor(
            fullHeight: Float,
            sheetHeight: Int
        ): Float? {
            return if (sheetHeight != 0) {
                0f
            } else null
        }

    };

    abstract fun hiddenAnchor(fullHeight: Float, sheetHeight: Int): Float?
    abstract fun halfExpandedAnchor(
        fullHeight: Float,
        sheetHeight: Int,
        isSkipHalfExpanded: Boolean,
    ): Float?

    abstract fun expandedAnchor(
        fullHeight: Float,
        sheetHeight: Int
    ): Float?


}

@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean,
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec(), label = "bottom_sheet_alpha"
        )
        val dismissModifier = if (visible) {
            Modifier
                .pointerInput(onDismiss) { detectTapGestures { onDismiss() } }
                .semantics(mergeDescendants = true) {
                    contentDescription = "Close sheet"
                    onClick { onDismiss(); true }
                }
        } else {
            Modifier
        }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

/**
 * Contains useful Defaults for [ModalSheetLayout].
 */
object ModalSheetDefaults {

    /**
     * The default elevation used by [ModalSheetLayout].
     */
    val Elevation = 16.dp

    /**
     * The default scrim color used by [ModalSheetLayout].
     */
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
}


private fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    state: SwipeableV2State<*>,
    orientation: Orientation
): NestedScrollConnection = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val delta = available.toFloat()
        return if (delta < 0 && source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(delta).toOffset()
        } else {
            Offset.Zero
        }
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return if (source == NestedScrollSource.Drag) {
            state.dispatchRawDelta(available.toFloat()).toOffset()
        } else {
            Offset.Zero
        }
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val toFling = available.toFloat()
        val currentOffset = state.requireOffset()
        return if (toFling < 0 && currentOffset > state.minOffset) {
            state.settle(velocity = toFling)
            // since we go to the anchor with tween settling, consume all for the best UX
            available
        } else {
            Velocity.Zero
        }
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        state.settle(velocity = available.toFloat())
        return available
    }

    private fun Float.toOffset(): Offset = Offset(
        x = if (orientation == Orientation.Horizontal) this else 0f,
        y = if (orientation == Orientation.Vertical) this else 0f
    )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}

private fun ModalBottomSheetAnchorChangeHandler(
    state: ModalSheetState,
    animateTo: (target: ModalSheetValue, velocity: Float) -> Unit,
    snapTo: (target: ModalSheetValue) -> Unit,
) = AnchorChangeHandler<ModalSheetValue> { previousTarget, previousAnchors, newAnchors ->
    val previousTargetOffset = previousAnchors[previousTarget]
    val newTarget = when (previousTarget) {
        Hidden -> Hidden
        HalfExpanded, Expanded -> {
            val hasHalfExpandedState =
                newAnchors.containsKey(HalfExpanded)
            val newTarget = if (hasHalfExpandedState) HalfExpanded
            else if (newAnchors.containsKey(Expanded)) Expanded else Hidden
            newTarget
        }
    }
    val newTargetOffset = newAnchors.getValue(newTarget)
    if (newTargetOffset != previousTargetOffset) {
        if (state.isAnimationRunning) {
            // Re-target the animation to the new offset if it changed
            animateTo(newTarget, state.lastVelocity)
        } else {
            // Snap to the new offset value of the target if no animation was running
            snapTo(newTarget)
        }
    }
}