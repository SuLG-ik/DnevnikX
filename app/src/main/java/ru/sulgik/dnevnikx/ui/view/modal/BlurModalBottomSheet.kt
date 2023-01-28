package ru.sulgik.dnevnikx.ui.view.modal

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import ru.sulgik.dnevnikx.ui.view.modal.BlurModalBottomSheetValue.*
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Possible values of [BlurModalBottomSheetState].
 */
enum class BlurModalBottomSheetValue {
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
 * State of the [BlurModalBottomSheetLayout] composable.
 *
 * @param initialValue The initial value of the state. <b>Must not be set to
 * [BlurModalBottomSheetValue.HalfExpanded] if [isSkipHalfExpanded] is set to true.</b>
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param isSkipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [BlurModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [BlurModalBottomSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
open class BlurModalBottomSheetState(
    initialValue: BlurModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    internal val isSkipHalfExpanded: Boolean,
    confirmStateChange: (BlurModalBottomSheetValue) -> Boolean = { true },
) : SwipeableState<BlurModalBottomSheetValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    /**
     * Whether the bottom sheet is visible.
     */
    val isVisible: Boolean
        get() = currentValue != Hidden

    internal val hasHalfExpandedState: Boolean
        get() = anchors.values.contains(HalfExpanded)

    constructor(
        initialValue: BlurModalBottomSheetValue,
        animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
        confirmStateChange: (BlurModalBottomSheetValue) -> Boolean = { true },
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
        try {
            animateTo(targetValue = targetValue)
        } catch (_: CancellationException) {
        }
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
        try {
            animateTo(HalfExpanded)
        } catch (_: CancellationException) {
        }
    }

    /**
     * Fully expand the bottom sheet with animation and suspend until it if fully expanded or
     * animation has been cancelled.
     * *
     * @throws [CancellationException] if the animation is interrupted
     */
    internal suspend fun expand() = try {
        animateTo(Expanded)
    } catch (_: CancellationException) {
    }

    /**
     * Hide the bottom sheet with animation and suspend until it if fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    suspend fun hide() = try {
        animateTo(Hidden)
    } catch (_: CancellationException) {
    }

    internal val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

    companion object {
        /**
         * The default [Saver] implementation for [BlurModalBottomSheetState].
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            skipHalfExpanded: Boolean,
            confirmStateChange: (BlurModalBottomSheetValue) -> Boolean,
        ): Saver<BlurModalBottomSheetState, *> = Saver(
            save = { it.currentValue },
            restore = {
                BlurModalBottomSheetState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    isSkipHalfExpanded = skipHalfExpanded,
                    confirmStateChange = confirmStateChange
                )
            }
        )

        /**
         * The default [Saver] implementation for [BlurModalBottomSheetState].
         */
        @Deprecated(
            message = "Please specify the skipHalfExpanded parameter",
            replaceWith = ReplaceWith(
                "ModalBottomSheetState.Saver(" +
                        "animationSpec = animationSpec," +
                        "skipHalfExpanded = ," +
                        "confirmStateChange = confirmStateChange" +
                        ")"
            )
        )
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (BlurModalBottomSheetValue) -> Boolean,
        ): Saver<BlurModalBottomSheetState, *> = Saver(
            animationSpec = animationSpec,
            skipHalfExpanded = false,
            confirmStateChange = confirmStateChange
        )
    }
}

/**
 * Create a [BlurModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param skipHalfExpanded Whether the half expanded state, if the sheet is tall enough, should
 * be skipped. If true, the sheet will always expand to the [Expanded] state and move to the
 * [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * <b>Must not be set to true if the [initialValue] is [BlurModalBottomSheetValue.HalfExpanded].</b>
 * If supplied with [BlurModalBottomSheetValue.HalfExpanded] for the [initialValue], an
 * [IllegalArgumentException] will be thrown.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable

fun rememberBlurModalBottomSheetState(
    initialValue: BlurModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    skipHalfExpanded: Boolean,
    confirmStateChange: (BlurModalBottomSheetValue) -> Boolean = { true },
): BlurModalBottomSheetState {
    return rememberSaveable(
        initialValue, animationSpec, skipHalfExpanded, confirmStateChange,
        saver = BlurModalBottomSheetState.Saver(
            animationSpec = animationSpec,
            skipHalfExpanded = skipHalfExpanded,
            confirmStateChange = confirmStateChange
        )
    ) {
        BlurModalBottomSheetState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            isSkipHalfExpanded = skipHalfExpanded,
            confirmStateChange = confirmStateChange
        )
    }
}

/**
 * Create a [BlurModalBottomSheetState] and [remember] it.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberBlurModalBottomSheetState(
    initialValue: BlurModalBottomSheetValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BlurModalBottomSheetValue) -> Boolean = { true },
): BlurModalBottomSheetState = rememberBlurModalBottomSheetState(
    initialValue = initialValue,
    animationSpec = animationSpec,
    skipHalfExpanded = false,
    confirmStateChange = confirmStateChange
)

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
fun BlurModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    blur: Dp = 16.dp,
    scale: Float = 0.98f,
    sheetState: BlurModalBottomSheetState =
        rememberBlurModalBottomSheetState(Hidden),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetPadding: Dp = 10.dp,
    sheetBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    BackHandler(enabled = sheetState.isVisible) {
        if (sheetState.confirmStateChange(Hidden)) {
            scope.launch { sheetState.hide() }
        }
    }
    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val sheetHeightState = remember { mutableStateOf<Float?>(null) }
        val scaleFactor =
            animateFloatAsState(if (sheetState.targetValue != Hidden) scale else 1f, tween(250))
        val blurFactor =
            animateDpAsState(if (sheetState.targetValue != Hidden) blur else 0.dp, tween(250))
        Box(
            Modifier
                .fillMaxSize()
                .blur(blurFactor.value)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scaleFactor.value)
            ) {
                content()
            }
            Scrim(
                color = scrimColor,
                onDismiss = {
                    if (sheetState.confirmStateChange(Hidden)) {
                        scope.launch { sheetState.hide() }
                    }
                },
                visible = sheetState.targetValue != Hidden
            )
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(sheetState.nestedScrollConnection)
                .offset {
                    val y = if (sheetState.anchors.isEmpty()) {
                        // if we don't know our anchors yet, render the sheet as hidden
                        fullHeight.roundToInt()
                    } else {
                        // if we do know our anchors, respect them
                        sheetState.offset.value.roundToInt()
                    }
                    IntOffset(0, y)
                }
                .bottomSheetSwipeable(sheetState, fullHeight, sheetHeightState)
                .onGloballyPositioned {
                    sheetHeightState.value = it.size.height.toFloat()
                }
                .semantics {
                    if (sheetState.isVisible) {
                        dismiss {
                            if (sheetState.confirmStateChange(Hidden)) {
                                scope.launch { sheetState.hide() }
                            }
                            true
                        }
                        if (sheetState.currentValue == HalfExpanded) {
                            expand {
                                if (sheetState.confirmStateChange(Expanded)) {
                                    scope.launch { sheetState.expand() }
                                }
                                true
                            }
                        } else if (sheetState.hasHalfExpandedState) {
                            collapse {
                                if (sheetState.confirmStateChange(HalfExpanded)) {
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

@Suppress("ModifierInspectorInfo")
private fun Modifier.bottomSheetSwipeable(
    sheetState: BlurModalBottomSheetState,
    fullHeight: Float,
    sheetHeightState: State<Float?>,
): Modifier {
    val sheetHeight = sheetHeightState.value
    val modifier = if (sheetHeight != null) {
        val anchors = if (sheetHeight < fullHeight / 2 || sheetState.isSkipHalfExpanded) {
            mapOf(
                fullHeight to Hidden,
                fullHeight - sheetHeight to Expanded
            )
        } else {
            mapOf(
                fullHeight to Hidden,
                fullHeight / 2 to HalfExpanded,
                max(0f, fullHeight - sheetHeight) to Expanded
            )
        }
        Modifier.swipeable(
            state = sheetState,
            anchors = anchors,
            orientation = Orientation.Vertical,
            enabled = sheetState.currentValue != Hidden,
            resistance = null
        )
    } else {
        Modifier
    }

    return this.then(modifier)
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
            animationSpec = TweenSpec()
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
 * Contains useful Defaults for [BlurModalBottomSheetLayout].
 */
object ModalBottomSheetDefaults {

    /**
     * The default elevation used by [BlurModalBottomSheetLayout].
     */
    val Elevation = 16.dp

    /**
     * The default scrim color used by [BlurModalBottomSheetLayout].
     */
    val scrimColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
}
