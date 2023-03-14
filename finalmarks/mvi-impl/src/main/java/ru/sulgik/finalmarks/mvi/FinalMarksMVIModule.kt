package ru.sulgik.finalmarks.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class FinalMarksMVIModule {

    val module = module {
        factoryOf(::FinalMarksStoreImpl) bind FinalMarksStore::class
    }

}