package ru.sulgik.schedule.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger
import ru.sulgik.schedule.domain.data.GetScheduleOutput

class MergedCachedScheduleAddRepository(
    private val localScheduleRepository: LocalScheduleRepository,
    private val remoteScheduleRepository: RemoteScheduleRepository,
) : CachedScheduleRepository {

    private val merger = Merger.named("ScheduleClass")

    override fun getSchedule(
        auth: AuthScope,
        classGroup: String
    ): FlowResource<GetScheduleOutput> {
        return merger.merged(
            localRequest = { localScheduleRepository.getSchedule(auth, classGroup) },
            save = { localScheduleRepository.saveSchedule(auth, it) },
            remoteRequest = { remoteScheduleRepository.getSchedule(auth, classGroup) }
        )
    }

    override fun getScheduleActual(
        auth: AuthScope,
        classGroup: String
    ): FlowResource<GetScheduleOutput> {
        return merger.remote(
            save = { localScheduleRepository.saveSchedule(auth, it) },
            remoteRequest = { remoteScheduleRepository.getSchedule(auth, classGroup) }
        )
    }

}