package ru.sulgik.account.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.sulgik.account.mvi.AccountStore
import ru.sulgik.ui.core.AnimatedContentWithPlaceholder
import ru.sulgik.ui.core.defaultPlaceholder
import ru.sulgik.ui.core.outlined


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    accountData: AccountStore.State.AccountData,
    actionsData: AccountStore.State.ActionsData,
    onSchedule: () -> Unit,
    onUpdates: () -> Unit,
    onFinalMarks: () -> Unit,
    onAbout: () -> Unit,
    onSelectAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text("Профиль")
            })
        },
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val account = accountData.account
                AnimatedContentWithPlaceholder(
                    isLoading = accountData.isLoading, state = account,
                    placeholderContent = {
                        ProfilePlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    },
                    content = {
                        Profile(
                            account = it,
                            onSelectAccount = onSelectAccount,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                AnimatedContentWithPlaceholder(
                    isLoading = actionsData.isLoading,
                    state = actionsData.actions,
                    placeholderContent = {
                        ProfileActionsPlaceholder(
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    content = {
                        ProfileActions(
                            actions = it,
                            onSchedule = onSchedule,
                            onUpdates = onUpdates,
                            onFinalMarks = onFinalMarks,
                            onAbout = onAbout,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun Profile(
    account: AccountStore.State.Account,
    onSelectAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Image(
            painterResource(id = R.drawable.student),
            contentDescription = "аватарка",
            modifier = Modifier
                .clip(CircleShape)
                .size(150.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelectAccount,
            )
        ) {
            Box(
                modifier = Modifier.size(20.dp)
            )
            Text(account.name, style = MaterialTheme.typography.bodyLarge)
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = "раскрыть",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProfilePlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(150.dp)
                .defaultPlaceholder()
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text("Имя Фамилия Отчество", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ProfileActions(
    actions: AccountStore.State.Actions,
    onSchedule: () -> Unit,
    onUpdates: () -> Unit,
    onFinalMarks: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (actions.isScheduleAvailable)
            ProfileAction(
                text = "Расписание",
                subtext = "Подробное описание уроков",
                icon = painterResource(id = R.drawable.timetable),
                onClick = onSchedule,
                modifier = Modifier.fillMaxWidth()
            )
        if (actions.isUpdatesAvailable)
            ProfileAction(
                text = "Обновления",
                subtext = "Изменения в журнале",
                icon = painterResource(id = R.drawable.updates),
                onClick = onUpdates,
                modifier = Modifier.fillMaxWidth()
            )
        if (actions.isFinalMarksAvailable)
            ProfileAction(
                text = "Итоговые оценки",
                subtext = "Сравнительная таблица по периодам",
                icon = painterResource(id = R.drawable.final_marks),
                onClick = onFinalMarks,
                modifier = Modifier.fillMaxWidth()
            )
        ProfileAction(
            text = "О приложении",
            subtext = actions.aboutData.applicationFullName,
            icon = painterResource(id = R.drawable.info),
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ProfileActionsPlaceholder(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileActionPlaceholder(modifier = Modifier.fillMaxWidth())
        ProfileActionPlaceholder(modifier = Modifier.fillMaxWidth())
        ProfileActionPlaceholder(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ProfileAction(
    text: String,
    subtext: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Image(
                icon, contentDescription = null, modifier = Modifier.size(40.dp),
            )
        },
        headlineContent = {
            Text(text, style = MaterialTheme.typography.titleMedium)
        }, supportingContent = {
            Text(subtext, style = MaterialTheme.typography.bodySmall)
        }, modifier = modifier
            .outlined()
            .clickable(onClick = onClick)
    )
}

@Composable
fun ProfileActionPlaceholder(
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = { Box(modifier = Modifier.size(40.dp)) },
        headlineContent = {
            Text(
                text = "Название блока",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.defaultPlaceholder(),
            )
        }, supportingContent = {
            Text(
                text = "Длинное описание блока",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.defaultPlaceholder()
            )
        }, modifier = modifier
            .outlined()
    )
}