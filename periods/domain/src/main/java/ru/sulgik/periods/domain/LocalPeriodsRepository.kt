package ru.sulgik.periods.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.periods.domain.data.GetPeriodsOutput

interface LocalPeriodsRepository {

    suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput?

    suspend fun savePeriods(auth: AuthScope, periods: GetPeriodsOutput)

}