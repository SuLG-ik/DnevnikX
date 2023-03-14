package ru.sulgik.common

import androidx.activity.ComponentActivity
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.common.platform.TimeFormatter
import ru.sulgik.common.platform.UriHandler
import ru.sulgik.common.platform.android.AndroidTimeFormatter
import ru.sulgik.common.platform.android.AndroidUriHandler

class AndroidModule {


    val module = module {
        scope<ComponentActivity> {
            scoped { AndroidUriHandler(get<ComponentActivity>()) } bind UriHandler::class
        }
        single { AndroidTimeFormatter() } bind TimeFormatter::class
    }

}