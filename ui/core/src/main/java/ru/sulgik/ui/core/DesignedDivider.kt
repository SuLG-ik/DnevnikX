package ru.sulgik.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DesignedDivider(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Spacer(
            modifier = Modifier
                .clip(CircleShape)
                .width(60.dp)
                .height(2.dp)
                .align(Alignment.Center)
                .background(
                    MaterialTheme.colorScheme.outline,
                )
        )
    }
}