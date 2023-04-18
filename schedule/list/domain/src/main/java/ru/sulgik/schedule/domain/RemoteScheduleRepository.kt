package ru.sulgik.schedule.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.schedule.domain.data.GetScheduleOutput

interface RemoteScheduleRepository {

    suspend fun getSchedule(
        auth: AuthScope,
        classGroup: String,
    ): GetScheduleOutput

}