package ru.sulgik.schedule.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.GetScheduleOutput

interface RemoteScheduleRepository {

    suspend fun getSchedule(
        auth: AuthScope,
        period: DatePeriod,
        classGroup: String,
    ): GetScheduleOutput

}