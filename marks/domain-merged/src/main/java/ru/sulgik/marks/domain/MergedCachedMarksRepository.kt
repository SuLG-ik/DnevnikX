package ru.sulgik.marks.domain

import io.github.aakira.napier.Napier
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.MarksOutput

class MergedCachedMarksRepository(
    private val localMarksRepository: LocalMarksRepository,
    private val remoteMarksRepository: RemoteMarksRepository,
) : CachedMarksRepository {

    override suspend fun getMarksFast(auth: AuthScope, period: DatePeriod): MarksOutput {
        val localPeriods = localMarksRepository.getMarks(auth, period)
        if (localPeriods != null) return localPeriods
        val remotePeriods = remoteMarksRepository.getMarks(auth, period)
        try {
            localMarksRepository.saveMarks(auth, period, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remotePeriods
    }

    override suspend fun getMarksActual(auth: AuthScope, period: DatePeriod): MarksOutput {
        val remotePeriods = remoteMarksRepository.getMarks(auth, period)
        try {
            localMarksRepository.saveMarks(auth, period, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remotePeriods
    }

}