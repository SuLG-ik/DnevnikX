package ru.sulgik.periods.domain

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

class PeriodsCachedModule {

    val module = module {
        singleOf(::MergedCachedPeriodsRepository) bind CachedPeriodsRepository::class
    }

}