package ru.sulgik.dnevnikx.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun EditableText(
    value: String,
    onChangeCompleted: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    cursorBrush: Brush = SolidColor(LocalContentColor.current),
    textStyle: TextStyle = LocalTextStyle.current.copy(color = LocalContentColor.current),
) {
    var text by rememberSaveable(value) { mutableStateOf(value) }
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && !focused) 0.85f else 1f)
    val focusRequester = LocalFocusManager.current
    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = modifier,
        textStyle = textStyle.copy(textDecoration = if (focused) TextDecoration.Underline else null),
        cursorBrush = cursorBrush,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onChangeCompleted(text)
            focusRequester.clearFocus()
        }),
        singleLine = true,
        maxLines = 1,
        enabled = enabled,
        interactionSource = interactionSource,
        decorationBox = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                it()
                AnimatedVisibility(
                    visible = !focused,
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(150)),
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(textStyle.color.copy(alpha = 0.12f))
                            .padding(3.dp)
                    )
                }
            }
        },
    )
}