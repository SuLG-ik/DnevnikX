package ru.sulgik.ui.core

import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable


@Suppress("PrivatePropertyName")
private val LowVelocityAnimationSpec = spring<Float>(
    stiffness = 15f,
)

@Suppress("PrivatePropertyName")
private val SnapAnimationSpec = spring<Float>(
    stiffness = 50f,
)

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun PagerState.flingBehaviour(): SnapFlingBehavior {
    return PagerDefaults.flingBehavior(
        state = this,
        lowVelocityAnimationSpec = LowVelocityAnimationSpec,
        snapAnimationSpec = SnapAnimationSpec,
    )
}