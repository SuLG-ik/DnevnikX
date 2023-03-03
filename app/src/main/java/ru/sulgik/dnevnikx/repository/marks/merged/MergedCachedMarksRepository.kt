package ru.sulgik.dnevnikx.repository.marks.merged

import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput
import ru.sulgik.dnevnikx.repository.marks.CachedMarksRepository
import ru.sulgik.dnevnikx.repository.marks.LocalMarksRepository
import ru.sulgik.dnevnikx.repository.marks.RemoteMarksRepository

@Single
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