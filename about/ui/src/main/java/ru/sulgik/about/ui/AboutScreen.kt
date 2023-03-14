package ru.sulgik.about.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.sulgik.about.mvi.AboutStore
import ru.sulgik.ui.core.outlined

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    aboutData: AboutStore.State.AboutData,
    backAvailable: Boolean,
    onDeveloper: () -> Unit,
    onDomain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "О приложении",
                )
            },
                navigationIcon = if (backAvailable) {
                    {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "назад")
                        }
                    }
                } else {
                    {}
                })
        },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AboutAppBlock(
                    data = aboutData.application,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    "Полезная информация",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LocalContentColor.current.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier)
                DomainBlock(
                    data = aboutData.domain,
                    onClick = onDomain,
                    modifier = Modifier.fillMaxWidth()
                )
                DeveloperBlock(
                    data = aboutData.developer,
                    onClick = onDeveloper,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppBlock(data: AboutStore.State.AboutData.ApplicationData, modifier: Modifier = Modifier) {
    ListItem(
        leadingContent = {
            Image(
                painterResource(id = R.drawable.about_dairy_icon),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
            )
        },
        headlineContent = {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("${data.name} ")
                }
                append(data.version)
            })
        },
        supportingContent = {
            Text(buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Неофициальный") }
                append(" клиент ЭлЖур")
            })
        },
        tonalElevation = 1.dp,
        modifier = modifier.outlined()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperBlock(
    data: AboutStore.State.AboutData.DeveloperData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Image(
                painterResource(id = R.drawable.about_developer),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
            )
        },
        headlineContent = {
            Text("Разработчик")
        },
        supportingContent = {
            Text(data.name)
        },
        modifier = modifier
            .outlined()
            .clickable(onClick = onClick)
    )
}

@Composable
fun DomainBlock(
    data: AboutStore.State.AboutData.DomainInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        leadingContent = {
            Image(
                painterResource(id = R.drawable.about_domain),
                contentDescription = null,
                modifier = Modifier.size(45.dp),
            )
        },
        headlineContent = {
            Text(data.name)
        },
        supportingContent = {
            Text(data.domain)
        },
        modifier = modifier
            .outlined()
            .clickable(onClick = onClick)
    )
}
