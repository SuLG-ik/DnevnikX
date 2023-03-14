package ru.sulgik.schedule.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleRemoteModule {

    val module = module {
        singleOf(::KtorRemoteScheduleRepository) bind RemoteScheduleRepository::class
    }

}