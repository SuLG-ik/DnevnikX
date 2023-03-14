package ru.sulgik.koin.main

import org.koin.dsl.module
import org.koin.ksp.generated.module
import ru.sulgik.auth.ktor.AuthClientKtorModule
import ru.sulgik.common.AndroidModule
import ru.sulgik.dnevnikx.BaseMviModule
import ru.sulgik.koin.domain.KoinDomainModule
import ru.sulgik.koin.mvi.KoinMVIModule
import ru.sulgik.room.auth.AuthDatabaseModule
import ru.sulgik.room.main.MainDatabaseModule

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
        )
    }

}