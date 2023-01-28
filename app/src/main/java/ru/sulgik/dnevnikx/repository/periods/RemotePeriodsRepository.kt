package ru.sulgik.dnevnikx.repository.periods

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput

interface RemotePeriodsRepository {

    suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput

}