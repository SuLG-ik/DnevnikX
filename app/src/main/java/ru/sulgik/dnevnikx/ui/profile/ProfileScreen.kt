package ru.sulgik.dnevnikx.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.ui.view.outlined


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSchedule: () -> Unit,
    onUpdates: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text("Профиль")
            })
        },
        modifier = modifier,
    ) {
        BoxWithConstraints(
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Profile(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )
                ProfileActions(
                    onSchedule = onSchedule,
                    onUpdates = onUpdates,
                    onAbout = onAbout,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}


@Composable
fun Profile(
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
        Text("Имя Фамилия Отчество", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ProfileActions(
    onSchedule: () -> Unit,
    onUpdates: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileAction(
            text = "Расписание",
            subtext = "Подробное описание уроков",
            icon = painterResource(id = R.drawable.timetable),
            onClick = onSchedule,
            modifier = Modifier.fillMaxWidth()
        )
        ProfileAction(
            text = "Обновления",
            subtext = "Изменения в журнале",
            icon = painterResource(id = R.drawable.updates),
            onClick = onUpdates,
            modifier = Modifier.fillMaxWidth()
        )
        ProfileAction(
            text = "О приложении",
            subtext = "DnevnikX v0.1",
            icon = painterResource(id = R.drawable.info),
            onClick = onAbout,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAction(
    text: String,
    subtext: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.ListItem(
        leadingContent = {
            Image(
                icon, contentDescription = null, modifier = Modifier.size(40.dp),
            )
        },
        headlineText = {
            Text(text, style = MaterialTheme.typography.titleMedium)
        }, supportingText = {
            Text(subtext, style = MaterialTheme.typography.bodySmall)
        }, modifier = modifier
            .outlined()
            .clickable(onClick = onClick)
    )
}