package ru.sulgik.dnevnikx

import android.content.ComponentCallbacks
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.decompose.defaultComponentContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.component.getScopeId
import org.koin.core.qualifier.TypeQualifier
import org.koin.core.scope.Scope
import ru.sulgik.common.platform.LocalDateProvider
import ru.sulgik.common.platform.LocalTimeFormatter
import ru.sulgik.core.withDI
import ru.sulgik.dnevnikx.ui.theme.DnevnikXExtendedTheme
import ru.sulgik.dnevnikx.ui.theme.DnevnikXTheme
import ru.sulgik.main.component.MainWithSplashComponent

class MainActivity : ComponentActivity(), AndroidScopeComponent {

    override val scope: Scope by lazy { createActivityScope() }

    override fun onCreate(savedInstanceState: Bundle?) {
        var component: MainWithSplashComponent? = null
        installSplashScreen().setKeepOnScreenCondition { component?.isLoading ?: true }
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        component = MainWithSplashComponent(defaultComponentContext().withDI(scope))
        setContent {
            DnevnikXExtendedTheme {
                DnevnikXTheme {
                    CompositionLocalProvider(
                        LocalTimeFormatter provides get(),
                        LocalDateProvider provides get(),
                    ) {
                        Surface {
                            component.Content(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }


    fun ComponentActivity.createActivityScope(): Scope {
        return getKoin().getScopeOrNull(getScopeId()) ?: createScopeForCurrentLifecycle(this)
    }

    private fun ComponentCallbacks.createScopeForCurrentLifecycle(owner: LifecycleOwner): Scope {
        val scope =
            getKoin().createScope(getScopeId(), TypeQualifier(ComponentActivity::class), this)
        owner.registerScopeForLifecycle(scope)
        return scope
    }

    private fun LifecycleOwner.registerScopeForLifecycle(
        scope: Scope,
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

}