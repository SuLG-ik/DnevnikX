package ru.sulgik.about.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class AboutMVIModule {

    val module = module {
        factoryOf(::AboutStoreImpl) bind AboutStore::class
    }

}