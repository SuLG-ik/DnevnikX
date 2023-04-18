package ru.sulgik.ui.component

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import ru.sulgik.core.BaseComponentContext
import trackScreen


val DefaultAnimation =
    fade(spring()) + scale(spring(), frontFactor = 0.8f, backFactor = 1.2f)

val LocalNestedChildrenStackAnimator = staticCompositionLocalOf<StackAnimator?> { DefaultAnimation }

val LocalChildrenStackAnimator = staticCompositionLocalOf { DefaultAnimation }

interface NamedConfig : Parcelable {

    val screenName: String?

}

@Composable
fun <Config : Parcelable> ChildStack<Config, BaseComponentContext>.Content(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(
        LocalChildrenStackAnimator.current
    ),
) {
    Children(stack = this, animation = animation) {
        it.instance.Content(modifier = modifier)
    }
}

@Composable
fun <Config : Parcelable> Value<ChildStack<Config, BaseComponentContext>>.Content(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(
        LocalChildrenStackAnimator.current
    ),
) {
    Children(stack = this, animation = animation) {
        it.instance.Content(modifier = modifier)
    }
}

@Composable
fun <Config : NamedConfig> Value<ChildStack<Config, BaseComponentContext>>.TrackedContent(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(
        LocalChildrenStackAnimator.current
    ),
) {
    Children(stack = this, animation = animation) {
        it.configuration.screenName?.let { screenName -> trackScreen(screenName) }
        it.instance.Content(modifier = modifier)
    }
}

@Composable
fun <Config : NamedConfig> ChildStack<Config, BaseComponentContext>.TrackedContent(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(
        LocalChildrenStackAnimator.current
    ),
) {
    Children(stack = this, animation = animation) {
        it.configuration.screenName?.let { screenName -> trackScreen(screenName) }
        it.instance.Content(modifier = modifier)
    }
}


@Composable
fun <Config : Parcelable> ChildSlot<Config, BaseComponentContext>.Content(
    modifier: Modifier = Modifier,
    placeholderContent: @Composable () -> Unit = {}
) {
    val instance = child?.instance
    if (instance != null) {
        instance.Content(modifier = modifier)
    } else {
        placeholderContent()
    }
}

@Composable
fun <Config : Parcelable> Value<ChildSlot<Config, BaseComponentContext>>.Content(
    modifier: Modifier = Modifier,
    placeholderContent: @Composable () -> Unit = {}
) {
    val instance = child?.instance
    if (instance != null) {
        instance.Content(modifier = modifier)
    } else {
        Box(modifier = modifier) {
            placeholderContent()
        }
    }
}