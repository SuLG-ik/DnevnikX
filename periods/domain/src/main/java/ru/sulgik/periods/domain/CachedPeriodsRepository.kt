package ru.sulgik.periods.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.periods.domain.data.GetPeriodsOutput

interface CachedPeriodsRepository {

    fun getPeriods(auth: AuthScope): FlowResource<GetPeriodsOutput>

    fun getPeriodsActual(auth: AuthScope): FlowResource<GetPeriodsOutput>

}