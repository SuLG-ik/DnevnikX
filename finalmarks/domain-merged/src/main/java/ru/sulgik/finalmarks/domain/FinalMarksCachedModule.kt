package ru.sulgik.finalmarks.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class FinalMarksCachedModule {

    val module = module {
        singleOf(::MergedCachedFinalMarksRepository) bind CachedFinalMarksRepository::class
    }

}