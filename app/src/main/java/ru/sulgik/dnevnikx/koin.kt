package ru.sulgik.dnevnikx

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import ru.sulgik.dnevnikx.mvi.StoresModule
import ru.sulgik.dnevnikx.repository.RepositoryModule
import ru.sulgik.dnevnikx.room.RoomModule

fun Application.setupDI() {
    startKoin {
        androidLogger()
        androidContext(this@setupDI)
        modules(
            ClientModule().module,
            BaseMviModule().module,
            RepositoryModule().module,
            StoresModule().module,
            RoomModule().module,
            AndroidModule().module,
        )
    }
}