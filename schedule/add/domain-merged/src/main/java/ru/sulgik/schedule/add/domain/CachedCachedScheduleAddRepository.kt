package ru.sulgik.schedule.add.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger
import ru.sulgik.schedule.add.domain.data.GetScheduleClassesOutput

class CachedCachedScheduleAddRepository(
    private val localScheduleClassRepository: LocalScheduleClassRepository,
) : CachedScheduleClassRepository {

    private val merger = Merger.named("ScheduleClass")

    override fun getClasses(auth: AuthScope): FlowResource<GetScheduleClassesOutput> {
        return merger.local(localFlow = { localScheduleClassRepository.getClasses(auth) })
    }

    override suspend fun addClass(
        auth: AuthScope,
        number: String,
        group: String,
    ) {
        localScheduleClassRepository.addClass(auth, number, group)
    }

    override suspend fun deleteClass(auth: AuthScope, number: String, group: String) {
        localScheduleClassRepository.deleteClass(auth, number, group)
    }
}