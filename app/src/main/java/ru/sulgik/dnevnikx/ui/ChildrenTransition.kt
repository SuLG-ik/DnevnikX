package ru.sulgik.dnevnikx.ui

import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable


@OptIn(ExperimentalDecomposeApi::class)
val LocalChildrenStackAnimator = staticCompositionLocalOf {
    fade(spring()) + scale(spring(), frontFactor = 0.8f, backFactor = 1.2f)
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <Config: Parcelable> ChildStack<Config, BaseComponentContext>.Content(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(LocalChildrenStackAnimator.current),
) {
    Children(stack = this, animation = animation) {
        it.instance.Content(modifier = modifier)
    }
}
@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun <Config: Parcelable> Value<ChildStack<Config, BaseComponentContext>>.Content(
    modifier: Modifier = Modifier,
    animation: StackAnimation<Config, BaseComponentContext>? = stackAnimation(LocalChildrenStackAnimator.current),
) {
    Children(stack = this, animation = animation) {
        it.instance.Content(modifier = modifier)
    }
}
