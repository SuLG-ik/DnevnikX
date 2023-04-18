package ru.sulgik.schedule.add.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

class RoomLocalScheduleClassRepository(
    private val dao: ScheduleClassDao,
) : LocalScheduleClassRepository {

    override fun getClasses(auth: AuthScope): Flow<GetScheduleClassesOutput> {
        return dao.getClasses(auth.id).map {
            GetScheduleClassesOutput(
                it.map { scheduleClass ->
                    GetScheduleClassesOutput.Class(
                        fullTitle = scheduleClass.number + scheduleClass.group,
                        number = scheduleClass.number,
                        group = scheduleClass.group,
                    )
                }
            )
        }
    }

    override suspend fun addClass(
        auth: AuthScope,
        number: String,
        group: String
    ) {
        dao.addClass(
            ScheduleClassEntity(
                accountId = auth.id,
                number = number,
                group = group,
            )
        )
    }

    override suspend fun deleteClass(auth: AuthScope, number: String, group: String) {
        dao.deleteClasses(auth.id, number, group)
    }

}