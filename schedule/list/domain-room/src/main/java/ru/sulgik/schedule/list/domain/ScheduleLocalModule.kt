package ru.sulgik.schedule.list.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.sulgik.schedule.domain.LocalScheduleRepository

class ScheduleLocalModule {

    val module = module {
        singleOf(::RoomLocalScheduleRepository) bind LocalScheduleRepository::class
    }

}