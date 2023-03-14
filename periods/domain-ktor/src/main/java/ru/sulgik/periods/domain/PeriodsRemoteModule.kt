package ru.sulgik.periods.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class PeriodsRemoteModule {

    val module = module {
        singleOf(::KtorRemotePeriodsRepository) bind RemotePeriodsRepository::class
    }

}