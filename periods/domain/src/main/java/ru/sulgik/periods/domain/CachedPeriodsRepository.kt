package ru.sulgik.periods.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.periods.domain.data.GetPeriodsOutput

interface CachedPeriodsRepository {

    suspend fun getPeriodsFast(auth: AuthScope): GetPeriodsOutput

    suspend fun getPeriodsActual(auth: AuthScope): GetPeriodsOutput

}