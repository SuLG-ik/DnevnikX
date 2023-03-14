package ru.sulgik.finalmarks.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class FinalMarksLocalModule {

    val module = module {
        singleOf(::RoomLocalFinalMarksRepository) bind LocalFinalMarksRepository::class
    }

}