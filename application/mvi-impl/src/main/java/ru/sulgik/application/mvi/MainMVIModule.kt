package ru.sulgik.application.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ApplicationMVIModule {

    val module = module {
        factoryOf(::ApplicationStoreImpl) bind ApplicationStore::class
    }

}