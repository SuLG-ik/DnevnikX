package ru.sulgik.koin.main

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


fun Application.setupKoin() {
    startKoin {
        androidLogger()
        androidContext(this@setupKoin)
        modules(
            KoinGeneralModule().module
        )
    }
}