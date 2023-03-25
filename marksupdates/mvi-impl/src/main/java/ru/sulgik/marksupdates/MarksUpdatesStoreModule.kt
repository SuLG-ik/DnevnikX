package ru.sulgik.marksupdates

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.marksupdates.mvi.MarksUpdatesStore

class MarksUpdatesMVIModule {

    val module = module {
        factoryOf(::MarksUpdatesStoreImpl) bind MarksUpdatesStore::class
    }

}