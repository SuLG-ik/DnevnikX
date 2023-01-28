package ru.sulgik.dnevnikx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.scope.Scope
import ru.sulgik.dnevnikx.ui.proxy.ProxyComponent
import ru.sulgik.dnevnikx.ui.theme.DnevnikXTheme
import ru.sulgik.dnevnikx.ui.withDI

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        var component: ProxyComponent? = null
        installSplashScreen().setKeepOnScreenCondition { component?.isLoading ?: true }
        super.onCreate(savedInstanceState)
        component = ProxyComponent(defaultComponentContext().withDI(scope))
        setContent {
            component.Content(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun GreetingPreview() {
    DnevnikXTheme {
        Greeting("Android")
    }
}