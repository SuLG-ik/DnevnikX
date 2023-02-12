package ru.sulgik.dnevnikx.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.ui.view.autofill
import ru.sulgik.dnevnikx.ui.view.pulse

val message = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
        append("Неофициальное")
    }
    append(" приложение сервиса school.nso.ru")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    isLoading: Boolean,
    username: String,
    password: String,
    onEditUsername: (String) -> Unit,
    onEditPassword: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .animateContentSize(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painterResource(id = R.drawable.dairy_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .pulse(isLoading)
            )
            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn(tween(500, delayMillis = 100)) + expandVertically(tween(500)),
                exit = fadeOut(tween(500)) + shrinkVertically(tween(500, delayMillis = 100)),
            ) {
                AuthInput(
                    username = username,
                    password = password,
                    onEditUsername = onEditUsername,
                    onEditPassword = onEditPassword,
                    onConfirm = onConfirm,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(300.dp),
                )
            }
        }
        Text(
            message,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AuthInput(
    username: String,
    password: String,
    onEditUsername: (String) -> Unit,
    onEditPassword: (String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = username,
            onValueChange = onEditUsername,
            placeholder = {
                Text("Имя пользователя")
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
        OutlinedTextField(
            value = password,
            onValueChange = onEditPassword,
            placeholder = {
                Text("Пароль")
            },
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier.fillMaxWidth()
                .autofill(AutofillType.Password, onFill = { onEditPassword(it) }),
        )
        OutlinedButton(onClick = onConfirm) {
            Text("Продолжить")
        }
    }
}