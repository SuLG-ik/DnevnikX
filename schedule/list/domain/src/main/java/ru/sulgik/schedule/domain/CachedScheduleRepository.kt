package ru.sulgik.schedule.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.schedule.domain.data.GetScheduleOutput

interface CachedScheduleRepository {

    fun getSchedule(
        auth: AuthScope,
        classGroup: String,
    ): FlowResource<GetScheduleOutput>

    fun getScheduleActual(
        auth: AuthScope,
        classGroup: String
    ): FlowResource<GetScheduleOutput>
}