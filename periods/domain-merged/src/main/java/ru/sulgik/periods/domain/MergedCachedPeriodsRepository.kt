package ru.sulgik.periods.domain

import io.github.aakira.napier.Napier
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.periods.domain.data.GetPeriodsOutput

class MergedCachedPeriodsRepository(
    private val remotePeriodsRepository: RemotePeriodsRepository,
    private val localPeriodsRepository: LocalPeriodsRepository,
) : CachedPeriodsRepository {

    override suspend fun getPeriodsFast(auth: AuthScope): GetPeriodsOutput {
        val localPeriods = localPeriodsRepository.getPeriods(auth)
        if (localPeriods != null) return localPeriods
        val remotePeriods = remotePeriodsRepository.getPeriods(auth)
        try {
            localPeriodsRepository.savePeriods(auth, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote periods to local error", e)
        }
        return remotePeriods
    }

    override suspend fun getPeriodsActual(auth: AuthScope): GetPeriodsOutput {
        val remotePeriods = remotePeriodsRepository.getPeriods(auth)
        try {
            localPeriodsRepository.savePeriods(auth, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote periods to local error", e)
        }
        return remotePeriods
    }

}