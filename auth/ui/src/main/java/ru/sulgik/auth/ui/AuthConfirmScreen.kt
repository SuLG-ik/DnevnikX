package ru.sulgik.auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.mvi.auth.AuthStore
import ru.sulgik.ui.core.ExtendedTheme

@Composable
fun AuthConfirmScreen(
    user: AuthStore.State.User,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(ExtendedTheme.dimensions.mainContentPadding),
    ) {
        Image(
            painter = painterResource(id = R.drawable.auth_student),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(100.dp),
        )
        Text(
            text = buildAnnotatedString {
                appendLine("Вывошли как")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(user.title)
                }
            },
            textAlign = TextAlign.Center,
        )
        Column {
            OutlinedButton(onClick = onCancel, modifier = Modifier.width(300.dp)) {
                Text(text = "Отменить", color = MaterialTheme.colorScheme.error)
            }
            OutlinedButton(onClick = onConfirm, modifier = Modifier.width(300.dp)) {
                Text(text = "Продолжить")
            }
        }
    }
}