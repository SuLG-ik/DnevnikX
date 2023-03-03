package ru.sulgik.dnevnikx.repository.diary.merged

import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput
import ru.sulgik.dnevnikx.repository.diary.CachedDiaryRepository
import ru.sulgik.dnevnikx.repository.diary.LocalDiaryRepository
import ru.sulgik.dnevnikx.repository.diary.RemoteDiaryRepository

@Single
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