package ru.sulgik.finalmarks.domain

import io.github.aakira.napier.Napier
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput

class MergedCachedFinalMarksRepository(
    private val localFinalMarksRepository: LocalFinalMarksRepository,
    private val remoteFinalMarksRepository: RemoteFinalMarksRepository,
) : CachedFinalMarksRepository {

    override suspend fun getFinalMarksFast(auth: AuthScope): FinalMarksOutput {
        val localResult = localFinalMarksRepository.getFinalMarks(auth)
        if (localResult != null) return localResult
        val remoteResult = remoteFinalMarksRepository.getFinalMarks(auth)
        try {
            localFinalMarksRepository.saveFinalMarks(auth, remoteResult)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remoteResult
    }

    override suspend fun getFinalMarksActual(auth: AuthScope): FinalMarksOutput {
        val remoteResult = remoteFinalMarksRepository.getFinalMarks(auth)
        try {
            localFinalMarksRepository.saveFinalMarks(auth, remoteResult)
        } catch (e: Exception) {
            Napier.e("Save remote diary to local error", e)
        }
        return remoteResult
    }
}