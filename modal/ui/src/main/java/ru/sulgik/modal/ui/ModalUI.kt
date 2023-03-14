package ru.sulgik.modal.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.sulgik.modal.component.Modal
import ru.sulgik.ui.modal.BlurModalBottomSheetValue
import ru.sulgik.ui.modal.ExtendedModalBottomSheet
import ru.sulgik.ui.modal.rememberBlurModalBottomSheetState

@Composable
fun FloatingModalUI(
    component: Modal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    content: @Composable () -> Unit,
) {
    val modalState = component.modalState
    val firstState = remember { modalState }
    val sheet =
        rememberBlurModalBottomSheetState(
            initialValue = if (firstState.isVisible) BlurModalBottomSheetValue.Expanded else BlurModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
            confirmStateChange = remember(component) {
                {
                    if (it == BlurModalBottomSheetValue.Hidden) {
                        component.updateState(false)
                    }
                    true
                }
            }
        )
    LaunchedEffect(component, modalState, sheet, block = {
        when {
            modalState.isVisible && !sheet.isVisible -> {
                sheet.show()
            }

            !modalState.isVisible && sheet.isVisible -> {
                sheet.hide()
            }
        }
    })
    ExtendedModalBottomSheet(
        sheetState = sheet,
        sheetContent = {
            component.Content(modifier = Modifier.fillMaxWidth())
        },
        sheetElevation = 4.dp,
        showDivider = showDivider,
        modifier = modifier,
        content = content,
    )
}

@Composable
fun ModalUI(
    component: Modal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    content: @Composable () -> Unit,
) {
    val modalState = component.modalState
    val firstState = remember { modalState }
    val sheet =
        rememberBlurModalBottomSheetState(
            initialValue = if (firstState.isVisible) BlurModalBottomSheetValue.Expanded else BlurModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
            confirmStateChange = remember(component) {
                {
                    if (it == BlurModalBottomSheetValue.Hidden) {
                        component.updateState(false)
                    }
                    true
                }
            }
        )
    LaunchedEffect(component, modalState, sheet, block = {
        when {
            modalState.isVisible && !sheet.isVisible -> {
                sheet.show()
            }

            !modalState.isVisible && sheet.isVisible -> {
                sheet.hide()
            }

            else -> {
                Log.d(
                    "ModalUI",
                    "State for $component does not affect to sheet state (modalState = ${modalState.isVisible}, sheet = ${sheet.isVisible}/${sheet.currentValue}) "
                )
            }
        }
    })
    ExtendedModalBottomSheet(
        sheetState = sheet,
        sheetContent = {
            component.Content(modifier = Modifier.fillMaxWidth())
        },
        sheetElevation = 0.dp,
        showDivider = showDivider,
        sheetPadding = 0.dp,
        modifier = modifier,
        sheetShape = MaterialTheme.shapes.extraLarge.copy(
            bottomEnd = ZeroCornerSize,
            bottomStart = ZeroCornerSize
        ),
        content = content,
    )
}