package ru.sulgik.schedule.add.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleAddCachedModule {

    val module = module {
        singleOf(::CachedCachedScheduleAddRepository) bind CachedScheduleClassRepository::class
    }

}