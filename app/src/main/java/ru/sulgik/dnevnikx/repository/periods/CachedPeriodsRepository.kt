package ru.sulgik.dnevnikx.repository.periods

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput

interface CachedPeriodsRepository {

    suspend fun getPeriodsFast(auth: AuthScope): GetPeriodsOutput

    suspend fun getPeriodsActual(auth: AuthScope): GetPeriodsOutput

}