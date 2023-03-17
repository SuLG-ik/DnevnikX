package ru.sulgik.diary.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DiaryMVIModule {

    val module = module {
        factoryOf(::DiaryStoreImpl) bind DiaryStore::class
        factoryOf(::DiarySettingsStoreImpl) bind DiarySettingsStore::class
    }

}