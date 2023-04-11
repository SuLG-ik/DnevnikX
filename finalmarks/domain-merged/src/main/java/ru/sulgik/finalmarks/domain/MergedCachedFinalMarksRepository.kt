package ru.sulgik.finalmarks.domain

import kotlinx.coroutines.flow.Flow
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput
import ru.sulgik.kacher.core.Merger
import ru.sulgik.kacher.core.Resource

class MergedCachedFinalMarksRepository(
    private val localFinalMarksRepository: LocalFinalMarksRepository,
    private val remoteFinalMarksRepository: RemoteFinalMarksRepository,
) : CachedFinalMarksRepository {

    private val merger: Merger = Merger.named("FinalMarks")

    override fun getFinalMarksActual(auth: AuthScope): Flow<Resource<FinalMarksOutput>> {
        return merger.remote(
            save = { localFinalMarksRepository.saveFinalMarks(auth, it) },
            remoteRequest = { remoteFinalMarksRepository.getFinalMarks(auth) })
    }


    override fun getFinalMarks(auth: AuthScope): Flow<Resource<FinalMarksOutput>> {
        return merger.merged(
            localRequest = { localFinalMarksRepository.getFinalMarks(auth) },
            save = { localFinalMarksRepository.saveFinalMarks(auth, it) },
            remoteRequest = { remoteFinalMarksRepository.getFinalMarks(auth) }
        )
    }

}