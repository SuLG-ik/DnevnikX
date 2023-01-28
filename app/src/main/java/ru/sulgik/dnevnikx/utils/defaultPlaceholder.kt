package ru.sulgik.dnevnikx.utils

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.defaultPlaceholder(visible: Boolean = true, shape: Shape? = null): Modifier {
    return composed {
        val color = LocalContentColor.current.copy(alpha = 0.1f)
        Modifier.placeholder(
            visible = visible,
            color = color,
            shape = shape ?: MaterialTheme.shapes.medium,
            highlight = PlaceholderHighlight.shimmer(color)
        )
    }
}