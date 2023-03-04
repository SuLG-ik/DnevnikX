package ru.sulgik.dnevnikx.repository.finalmarks.merged

import io.github.aakira.napier.Napier
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.FinalMarksOutput
import ru.sulgik.dnevnikx.repository.finalmarks.CachedFinalMarksRepository
import ru.sulgik.dnevnikx.repository.finalmarks.LocalFinalMarksRepository
import ru.sulgik.dnevnikx.repository.finalmarks.RemoteFinalMarksRepository

@Single
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