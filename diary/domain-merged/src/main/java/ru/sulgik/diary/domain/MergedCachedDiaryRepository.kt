package ru.sulgik.diary.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger

class MergedCachedDiaryRepository(
    private val localDiaryRepository: LocalDiaryRepository,
    private val remoteDiaryRepository: RemoteDiaryRepository,
) : CachedDiaryRepository {

    private val merger: Merger = Merger.named("Diary")

    override fun getDiaryOld(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput> {
        return merger.local(localRequest = { localDiaryRepository.getDiary(auth, period) })
    }

    override fun getDiaryActual(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput> {
        return merger.remote(save = { localDiaryRepository.saveDiary(auth, it) },
            remoteRequest = { remoteDiaryRepository.getDiary(auth, period) })
    }

    override fun getDiary(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput> {
        return merger.merged(localRequest = { localDiaryRepository.getDiary(auth, period) },
            save = { localDiaryRepository.saveDiary(auth, it) },
            remoteRequest = { remoteDiaryRepository.getDiary(auth, period) })
    }


}