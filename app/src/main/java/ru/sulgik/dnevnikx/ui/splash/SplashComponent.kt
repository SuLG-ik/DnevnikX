package ru.sulgik.dnevnikx.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.sulgik.dnevnikx.R
import ru.sulgik.dnevnikx.ui.BaseComponentContext
import ru.sulgik.dnevnikx.ui.DIComponentContext
import ru.sulgik.dnevnikx.ui.view.pulse

class SplashComponent(componentContext: DIComponentContext) :
    BaseComponentContext(componentContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(id = R.drawable.dairy_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
            )
        }
    }
}