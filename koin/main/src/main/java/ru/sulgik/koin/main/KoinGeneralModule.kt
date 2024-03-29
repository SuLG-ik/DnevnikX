package ru.sulgik.koin.main

import org.koin.dsl.module
import org.koin.ksp.generated.module
import ru.sulgik.auth.ktor.AuthClientKtorModule
import ru.sulgik.common.AndroidModule
import ru.sulgik.dnevnikx.BaseMviModule
import ru.sulgik.firebase.main.FirebaseModule
import ru.sulgik.images.impl.ImagesLoaderModule
import ru.sulgik.koin.domain.KoinDomainModule
import ru.sulgik.koin.mvi.KoinMVIModule
import ru.sulgik.koin.settings.KoinSettingsModule
import ru.sulgik.ktor.main.KtorModule
import ru.sulgik.room.auth.AuthDatabaseModule
import ru.sulgik.room.main.MainDatabaseModule
import ru.sulgik.settings.settings.datastore.SettingsProviderModule

class KoinGeneralModule {

    val module = module {
        includes(
            KoinMVIModule().module,
            KoinDomainModule().module,
            KtorModule().module,
            MainDatabaseModule().module,
            AuthDatabaseModule().module,
            AndroidModule().module,
            BaseMviModule().module,
            AuthClientKtorModule().module,
            SettingsProviderModule().module,
            KoinSettingsModule().module,
            FirebaseModule().module,
            ImagesLoaderModule().module,
        )
    }

}