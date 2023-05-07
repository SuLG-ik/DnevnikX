package ru.sulgik.auth.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.sulgik.auth.mvi.AuthStore
import ru.sulgik.images.ui.SubcomponentRemoteImage
import ru.sulgik.ui.core.ExtendedTheme
import ru.sulgik.ui.core.autofill
import ru.sulgik.ui.core.outlined
import ru.sulgik.ui.core.pulse

val message = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("Неофициальное")
    }
    append(" приложение сервиса «ЭлЖур»")
}

@Composable
fun AuthScreen(
    state: AuthStore.State,
    isVendorSelecting: Boolean,
    isBackAvailable: Boolean,
    onEditUsername: (String) -> Unit,
    onEditPassword: (String) -> Unit,
    onSelectVendor: () -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val authContent = @Composable { isVertical: Boolean ->
                Image(
                    painterResource(id = R.drawable.auth_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .pulse(state.isLoading)
                )
                AnimatedVisibility(
                    visible = !state.isLoading,
                    enter = fadeIn(
                        tween(
                            500,
                            delayMillis = 100
                        )
                    ) + if (isVertical) expandVertically(tween(500)) else expandHorizontally(
                        tween(
                            500
                        )
                    ),
                    exit = fadeOut(tween(500)) + if (isVertical) shrinkVertically(
                        tween(
                            500,
                            delayMillis = 100
                        )
                    ) else shrinkHorizontally(tween(500, delayMillis = 100)),
                ) {
                    AuthField(
                        authState = state.authField,
                        vendorSelector = state.vendorSelector,
                        isContinueAvailable = state.isContinueAvailable,
                        isVendorSelecting = isVendorSelecting,
                        onEditUsername = onEditUsername,
                        onEditPassword = onEditPassword,
                        onConfirm = onConfirm,
                        onSelectVendor = onSelectVendor,
                        onBack = if (isBackAvailable) onBack else null,
                        modifier = Modifier
                            .padding(
                                top = if (isVertical) 20.dp else 0.dp,
                                start = if (isVertical) 0.dp else 20.dp,
                            )
                            .padding(horizontal = 10.dp)
                            .width(300.dp),
                    )
                }
            }
            if (LocalConfiguration.current.screenWidthDp >= 600) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    authContent(false)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    authContent(true)
                }
            }

        }
        Text(
            text = message,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthField(
    authState: AuthStore.State.AuthField,
    vendorSelector: AuthStore.State.VendorSelector,
    isContinueAvailable: Boolean,
    isVendorSelecting: Boolean,
    onEditUsername: (String) -> Unit,
    onEditPassword: (String) -> Unit,
    onSelectVendor: () -> Unit,
    onConfirm: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val focusManager = LocalFocusManager.current
        Text(
            text = authState.error ?: "",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error,
        )
        VendorSelectorField(
            isVendorSelecting = isVendorSelecting,
            selectedVendor = vendorSelector.selectedVendor,
            onSelectVendor = {
                onSelectVendor()
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            modifier = Modifier.fillMaxWidth(),
        )
        AuthFieldInput(
            username = authState.username,
            password = authState.password,
            onEditUsername = onEditUsername,
            onEditPassword = onEditPassword,
            onConfirm = onConfirm,
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
        )
        AuthConfirm(
            isContinueAvailable = isContinueAvailable,
            onConfirm = onConfirm,
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun AuthConfirm(
    isContinueAvailable: Boolean,
    onConfirm: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            ExtendedTheme.dimensions.contentSpaceBetween,
            Alignment.CenterHorizontally
        ),
        modifier = modifier,
    ) {
        if (onBack != null) {
            OutlinedIconButton(
                colors = IconButtonDefaults.outlinedIconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = ButtonDefaults.outlinedButtonBorder,
                onClick = onBack
            ) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "назад")
            }
        }
        OutlinedButton(onClick = onConfirm, enabled = isContinueAvailable) {
            Text("Продолжить")
        }
        if (onBack != null) {
            Box(
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AuthFieldInput(
    username: String,
    password: String,
    onEditUsername: (String) -> Unit,
    onEditPassword: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(ExtendedTheme.dimensions.contentSpaceBetween),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = username,
            onValueChange = onEditUsername,
            placeholder = {
                Text("Имя пользователя")
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.auth_username),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 10.dp),
                )
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Next)
            }),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .autofill(AutofillType.Username, onFill = { onEditUsername(it) }),
        )
        var isPasswordHidden by rememberSaveable {
            mutableStateOf(true)
        }
        OutlinedTextField(
            value = password,
            onValueChange = onEditPassword,
            placeholder = {
                Text("Пароль")
            },
            leadingIcon = {
                Icon(
                    painterResource(id = R.drawable.auth_password),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(start = 10.dp),
                )
            },
            trailingIcon = {
                AnimatedContent(
                    isPasswordHidden,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "password_hidden",
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .clip(CircleShape)
                        .clickable(onClick = { isPasswordHidden = !isPasswordHidden })
                        .padding(5.dp)
                ) {
                    if (it) {
                        Icon(
                            painterResource(id = R.drawable.auth_password_hiden),
                            contentDescription = "пароль скрыт",
                            modifier = Modifier.size(25.dp),
                        )
                    } else {
                        Icon(
                            painterResource(id = R.drawable.auth_password_shown),
                            contentDescription = "пароль показан",
                            modifier = Modifier.size(25.dp),
                        )
                    }
                }
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (isPasswordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .autofill(AutofillType.Password, onFill = { onEditPassword(it) }),
        )
    }
}


@Composable
private fun VendorSelectorField(
    isVendorSelecting: Boolean,
    selectedVendor: AuthStore.State.Vendor?,
    onSelectVendor: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .outlined(
                shape = MaterialTheme.shapes.extraLarge,
                color = animateColorAsState(
                    if (isVendorSelecting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    label = "vendor_selector_outline_color"
                ).value,
                width = animateDpAsState(
                    if (isVendorSelecting) 2.dp else 1.dp,
                    label = "vendor_selector_outline_width"
                ).value,
            )
            .clickable(onClick = onSelectVendor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(15.dp))
        VendorImage(
            vendor = selectedVendor,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AnimatedContent(
            targetState = selectedVendor?.realName,
            label = "vendor_name",
            transitionSpec = { fadeIn() togetherWith fadeOut() }) {
            if (it != null) {
                Text(it, maxLines = 1)
            } else {
                Text(
                    "Регион не выбран",
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Composable
fun VendorSelectorScreen(
    vendors: ImmutableList<AuthStore.State.Vendor>,
    onSelectVendor: (AuthStore.State.Vendor) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        vendors.forEach {
            VendorSelectorItemScreen(
                vendor = it,
                onSelectVendor = { onSelectVendor(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun VendorSelectorItemScreen(
    vendor: AuthStore.State.Vendor,
    onSelectVendor: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        leadingContent = {
            VendorImage(
                vendor = vendor,
                modifier = Modifier.size(35.dp),
            )
        },
        headlineContent = {
            Text(vendor.realName)
        },
        modifier = modifier.clickable(onClick = onSelectVendor)
    )
}

@Composable
private fun VendorImage(
    vendor: AuthStore.State.Vendor?,
    modifier: Modifier = Modifier
) {
    SubcomponentRemoteImage(
        model = vendor?.logo,
        contentDescription = null,
        modifier = modifier,
        error = {
            Icon(
                painterResource(id = R.drawable.auth_location),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}