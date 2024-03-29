package ru.sulgik.modal.ui

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import ru.sulgik.modal.component.Modal
import ru.sulgik.ui.modal.ExtendedModalBottomSheet
import ru.sulgik.ui.modal.ModalSheetDirection
import ru.sulgik.ui.modal.ModalSheetValue
import ru.sulgik.ui.modal.rememberModalSheetState

@Composable
fun FloatingModalUI(
    component: Modal,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    direction: ModalSheetDirection = ModalSheetDirection.BOTTOM,
    content: @Composable () -> Unit,
) {
    val modalState = component.modalState
    val firstState = remember { modalState }
    val sheet =
        rememberModalSheetState(
            initialValue = if (firstState.isVisible) ModalSheetValue.Expanded else ModalSheetValue.Hidden,
            skipHalfExpanded = true,
            confirmValueChange = remember(component) {
                {
                    if (it == ModalSheetValue.Hidden) {
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
        direction = direction,
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
    direction: ModalSheetDirection = ModalSheetDirection.BOTTOM,
    content: @Composable () -> Unit,
) {
    val modalState = component.modalState
    val firstState = remember { modalState }
    val sheet =
        rememberModalSheetState(
            initialValue = if (firstState.isVisible) ModalSheetValue.Expanded else ModalSheetValue.Hidden,
            skipHalfExpanded = true,
            confirmValueChange = remember(component) {
                {
                    if (it == ModalSheetValue.Hidden) {
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
        direction = direction,
        sheetContent = {
            component.Content(modifier = Modifier.fillMaxWidth())
        },
        sheetElevation = if (LocalConfiguration.current.screenWidthDp <= 650) 0.dp else 4.dp,
        showDivider = showDivider,
        sheetPadding = if (LocalConfiguration.current.screenWidthDp <= 650) 0.dp else 10.dp,
        modifier = modifier,
        sheetShape = if (LocalConfiguration.current.screenWidthDp <= 650) MaterialTheme.shapes.extraLarge.copy(
            bottomEnd = ZeroCornerSize,
            bottomStart = ZeroCornerSize
        ) else MaterialTheme.shapes.extraLarge,
        content = content,
    )
}