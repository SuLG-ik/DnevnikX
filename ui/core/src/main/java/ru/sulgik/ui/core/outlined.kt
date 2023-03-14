package ru.sulgik.ui.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.outlined(
    color: Color? = null,
    shape: Shape? = null,
) =
    composed(
        inspectorInfo = {
            name = "outlined"
            properties["shape"] = shape
            properties["color"] = color
        }
    ) {
        val baseShape = shape ?: MaterialTheme.shapes.large
        val baseColor = color ?: MaterialTheme.colorScheme.outline
        Modifier
            .clip(baseShape)
            .border(
                border = BorderStroke(width = 1.dp, color = baseColor),
                shape = baseShape,
            )
    }