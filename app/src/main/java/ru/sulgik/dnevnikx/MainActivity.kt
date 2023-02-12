package ru.sulgik.dnevnikx

import android.content.ComponentCallbacks
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.component.getScopeId
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import ru.sulgik.dnevnikx.platform.LocalTimeFormatter
import ru.sulgik.dnevnikx.platform.android.AndroidTimeFormatter
import ru.sulgik.dnevnikx.ui.proxy.ProxyComponent
import ru.sulgik.dnevnikx.ui.theme.DnevnikXTheme
import ru.sulgik.dnevnikx.ui.withDI

class MainActivity : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by lazy { createActivityScope() }

    override fun onCreate(savedInstanceState: Bundle?) {
        var component: ProxyComponent? = null
        installSplashScreen().setKeepOnScreenCondition { component?.isLoading ?: true }
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        component = ProxyComponent(defaultComponentContext().withDI(scope))
        val formatter = AndroidTimeFormatter()
        setContent {
            CompositionLocalProvider(
                LocalTimeFormatter provides formatter
            ) {
                component.Content(modifier = Modifier.fillMaxSize())
            }
        }
    }
}


fun AppCompatActivity.createActivityScope(): Scope {
    return getKoin().getScopeOrNull(getScopeId()) ?: createScopeForCurrentLifecycle(this)
}

private fun ComponentCallbacks.createScopeForCurrentLifecycle(owner: LifecycleOwner): Scope {
    val scope = getKoin().createScope(getScopeId(), TypeQualifier(AppCompatActivity::class), this)
    owner.registerScopeForLifecycle(scope)
    return scope
}

internal fun LifecycleOwner.registerScopeForLifecycle(
    scope: Scope
) {
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                scope.close()
            }
        }
    )
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