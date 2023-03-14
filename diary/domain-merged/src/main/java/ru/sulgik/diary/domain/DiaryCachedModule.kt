package ru.sulgik.diary.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DiaryCachedModule {

    val module = module {
        singleOf(::MergedCachedDiaryRepository) bind CachedDiaryRepository::class
    }

}