package ru.sulgik.splash.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.sulgik.core.BaseComponentContext
import ru.sulgik.core.DIComponentContext
import ru.sulgik.splash.ui.SplashScreen

class SplashComponent(componentContext: DIComponentContext) :
    BaseComponentContext(componentContext) {

    @Composable
    override fun Content(modifier: Modifier) {
        SplashScreen(modifier = modifier)
    }

}