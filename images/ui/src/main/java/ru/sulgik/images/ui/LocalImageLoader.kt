package ru.sulgik.images.ui

import androidx.compose.runtime.staticCompositionLocalOf
import coil.ImageLoader

val LocalImageLoader = staticCompositionLocalOf<ImageLoader> { error("ImageLoader is not set") }