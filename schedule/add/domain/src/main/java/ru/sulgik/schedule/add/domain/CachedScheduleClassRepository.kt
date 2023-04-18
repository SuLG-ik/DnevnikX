package ru.sulgik.schedule.add.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

interface CachedScheduleClassRepository {

    fun getClasses(
        auth: AuthScope,
    ): FlowResource<GetScheduleClassesOutput>

    suspend fun addClass(auth: AuthScope, number: String, group: String)

    suspend fun deleteClass(auth: AuthScope, number: String, group: String)


}