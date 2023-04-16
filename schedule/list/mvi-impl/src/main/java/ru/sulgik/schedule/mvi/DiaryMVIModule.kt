package ru.sulgik.schedule.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleMVIModule {

    val module = module {
        factoryOf(::ScheduleStoreImpl) bind ScheduleStore::class
    }

}