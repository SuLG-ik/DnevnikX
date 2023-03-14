package ru.sulgik.account.selector.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.sulgik.account.selector.mvi.AccountSelectorStore

@Composable
fun AccountSelectorScreen(
    accounts: List<AccountSelectorStore.State.Account>,
    onAccountSelected: (AccountSelectorStore.State.Account) -> Unit,
    onAddAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 10.dp)
    ) {
        accounts.forEach {
            ProfileItem(
                name = it.name,
                onClick = { onAccountSelected(it) },
                selected = it.selected,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AddAccountItem(onAddAccount, modifier = Modifier.fillMaxWidth())
    }
}


@Composable
fun ProfileItem(
    name: String,
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(name)
        },
        leadingContent = {
            Image(
                painterResource(id = R.drawable.student),
                contentDescription = "Иконка",
                modifier = Modifier
                    .conditionBorder(
                        enabled = selected,
                        width = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .size(55.dp)
                    .clip(CircleShape),
            )
        },
        modifier = modifier
            .clickable(onClick = onClick)
    )
}

@Composable
fun AddAccountItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text("Добавить аккаунт")
        },
        leadingContent = {
            Image(
                painterResource(id = R.drawable.profile_selector_add),
                contentDescription = "Иконка",
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape),
            )
        },
        modifier = modifier
            .clickable(onClick = onClick)
    )
}

private fun Modifier.conditionBorder(
    enabled: Boolean,
    width: Dp,
    color: Color,
    shape: Shape = RectangleShape,
): Modifier {
    if (!enabled) return this
    return border(width, color, shape)
}