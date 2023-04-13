package ru.sulgik.periods.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger
import ru.sulgik.periods.domain.data.GetPeriodsOutput

class MergedCachedPeriodsRepository(
    private val remotePeriodsRepository: RemotePeriodsRepository,
    private val localPeriodsRepository: LocalPeriodsRepository,
) : CachedPeriodsRepository {

    private val merger = Merger.named("Periods")

    override fun getPeriods(auth: AuthScope): FlowResource<GetPeriodsOutput> {
        return merger.merged(
            localRequest = { localPeriodsRepository.getPeriods(auth) },
            save = { localPeriodsRepository.savePeriods(auth, it) },
            remoteRequest = { remotePeriodsRepository.getPeriods(auth) }
        )
    }

    override fun getPeriodsActual(auth: AuthScope): FlowResource<GetPeriodsOutput> {
        return merger.remote(
            save = { localPeriodsRepository.savePeriods(auth, it) },
            remoteRequest = { remotePeriodsRepository.getPeriods(auth) },
        )
    }

}