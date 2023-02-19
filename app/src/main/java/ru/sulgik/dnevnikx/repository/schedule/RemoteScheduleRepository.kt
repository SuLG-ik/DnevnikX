package ru.sulgik.dnevnikx.repository.schedule

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.GetScheduleOutput

interface RemoteScheduleRepository {

    suspend fun getSchedule(auth: AuthScope, period: DatePeriod, classGroup: String): GetScheduleOutput

}