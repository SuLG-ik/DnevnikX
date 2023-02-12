package ru.sulgik.dnevnikx

import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.dnevnikx.platform.UriHandler
import ru.sulgik.dnevnikx.platform.android.AndroidUriHandler

class AndroidModule {


    val module = module {
        scope<AppCompatActivity> {
            scoped { AndroidUriHandler(get<AppCompatActivity>()) } bind UriHandler::class
        }
    }

}