package ru.sulgik.diary.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class DiaryLocalModule {

    val module = module {
        singleOf(::RoomLocalDiaryRepository) bind LocalDiaryRepository::class
    }

}