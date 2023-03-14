package ru.sulgik.periods.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.periods.domain.data.GetPeriodsOutput

interface RemotePeriodsRepository {

    suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput

}