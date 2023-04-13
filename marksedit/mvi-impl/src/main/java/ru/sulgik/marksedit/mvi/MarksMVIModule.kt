package ru.sulgik.marksedit.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class MarksEditMVIModule {

    val module = module {
        factoryOf(::MarksEditStoreImpl) bind MarksEditStore::class
    }

}