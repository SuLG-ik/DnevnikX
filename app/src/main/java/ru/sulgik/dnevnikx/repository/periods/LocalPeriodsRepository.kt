package ru.sulgik.dnevnikx.repository.periods

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput

interface LocalPeriodsRepository {

    suspend fun getPeriods(auth: AuthScope): GetPeriodsOutput?

    suspend fun savePeriods(auth: AuthScope, periods: GetPeriodsOutput)

}