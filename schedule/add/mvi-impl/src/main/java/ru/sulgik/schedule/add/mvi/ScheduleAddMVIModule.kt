package ru.sulgik.schedule.add.mvi

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

class ScheduleAddMVIModule {

    val module = module {
        factoryOf(::ScheduleClassesEditStoreImpl) bind ScheduleClassesEditStore::class
    }

}