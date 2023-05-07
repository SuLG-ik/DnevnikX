package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger

class MergedCachedFinalMarksRepository(
    private val localFinalMarksRepository: LocalFinalMarksRepository,
    private val remoteFinalMarksRepository: RemoteFinalMarksRepository,
) : CachedFinalMarksRepository {


    private val merger: Merger = Merger.named("FinalMarks")

    override fun getFinalMarksActual(auth: AuthScope): FlowResource<FinalMarksOutput> {
        return merger.remote(
            save = { localFinalMarksRepository.saveFinalMarks(auth, it) }
        ) { remoteFinalMarksRepository.getFinalMarks(auth) }
    }


    override fun getFinalMarks(auth: AuthScope): FlowResource<FinalMarksOutput> {
        return merger.merged(
            localRequest = { localFinalMarksRepository.getFinalMarks(auth) },
            save = { localFinalMarksRepository.saveFinalMarks(auth, it) }
        ) { remoteFinalMarksRepository.getFinalMarks(auth) }
    }

}