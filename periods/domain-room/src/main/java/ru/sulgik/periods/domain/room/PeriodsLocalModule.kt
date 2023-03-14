package ru.sulgik.periods.domain.room

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.periods.domain.LocalPeriodsRepository

class PeriodsLocalModule {

    val module = module {
        singleOf(::RoomLocalPeriodsRepository) bind LocalPeriodsRepository::class
    }

}