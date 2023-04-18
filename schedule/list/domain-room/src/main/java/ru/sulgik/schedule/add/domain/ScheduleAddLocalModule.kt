package ru.sulgik.schedule.add.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleAddLocalModule {

    val module = module {
        singleOf(::RoomLocalScheduleClassRepository) bind LocalScheduleClassRepository::class
    }

}