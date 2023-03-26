package ru.sulgik.analytics.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

@SuppressLint("ComposableNaming")
@Composable
fun trackScreen(
    name: String,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                    param(FirebaseAnalytics.Param.SCREEN_NAME, "${name}_screen")
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}