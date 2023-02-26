package ru.sulgik.dnevnikx.repository.periods.merged

import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.GetPeriodsOutput
import ru.sulgik.dnevnikx.repository.periods.CachedPeriodsRepository
import ru.sulgik.dnevnikx.repository.periods.LocalPeriodsRepository
import ru.sulgik.dnevnikx.repository.periods.RemotePeriodsRepository

@Single
class CachedRemotePeriodsRepository(
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