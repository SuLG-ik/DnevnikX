package ru.sulgik.marks.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.dnevnikx.mvi.marks.MarksStore

class MarksMVIModule {

    val module = module {
        factoryOf(::MarksStoreImpl) bind MarksStore::class
    }

}