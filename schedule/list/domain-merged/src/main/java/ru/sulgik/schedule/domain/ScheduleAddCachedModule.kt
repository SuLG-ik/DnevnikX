package ru.sulgik.schedule.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleCachedModule {

    val module = module {
        singleOf(::MergedCachedScheduleAddRepository) bind CachedScheduleRepository::class
    }

}