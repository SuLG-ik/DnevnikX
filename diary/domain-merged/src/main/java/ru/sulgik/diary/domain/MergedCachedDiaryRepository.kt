package ru.sulgik.diary.domain

import io.github.aakira.napier.Napier
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput

class MergedCachedDiaryRepository(
    private val localDiaryRepository: LocalDiaryRepository,
    private val remoteDiaryRepository: RemoteDiaryRepository,
) : CachedDiaryRepository {
    override suspend fun getDiaryFast(auth: AuthScope, period: DatePeriod): DiaryOutput {
        val localPeriods = localDiaryRepository.getDiary(auth, period)
        if (localPeriods != null) return localPeriods
        val remotePeriods = remoteDiaryRepository.getDiary(auth, period)
        try {
            localDiaryRepository.saveDiary(auth, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remotePeriods
    }

    override suspend fun getDiaryActual(auth: AuthScope, period: DatePeriod): DiaryOutput {
        val remotePeriods = remoteDiaryRepository.getDiary(auth, period)
        try {
            localDiaryRepository.saveDiary(auth, remotePeriods)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remotePeriods
    }


}