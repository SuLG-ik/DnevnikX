package ru.sulgik.schedule.add.domain

import kotlinx.coroutines.flow.Flow
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

interface LocalScheduleClassRepository {

    fun getClasses(
        auth: AuthScope,
    ): Flow<GetScheduleClassesOutput>

    suspend fun addClass(auth: AuthScope, number: String, group: String)

    suspend fun deleteClass(auth: AuthScope, number: String, group: String)

}