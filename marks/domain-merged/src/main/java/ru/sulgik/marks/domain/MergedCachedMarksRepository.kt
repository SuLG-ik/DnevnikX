package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger
import ru.sulgik.marks.domain.data.LessonOutput
import ru.sulgik.marks.domain.data.MarksOutput

class MergedCachedMarksRepository(
    private val localMarksRepository: LocalMarksRepository,
    private val remoteMarksRepository: RemoteMarksRepository,
) : CachedMarksRepository {

    private val merger = Merger.named("Marks")

    override fun getMarksOld(
        auth: AuthScope,
        period: MarksOutput.Period
    ): FlowResource<MarksOutput> {
        return merger.local(localRequest = { localMarksRepository.getMarks(auth, period.period) })
    }

    override fun getMarks(auth: AuthScope, period: MarksOutput.Period): FlowResource<MarksOutput> {
        return merger.merged(
            localRequest = { localMarksRepository.getMarks(auth, period.period) },
            save = { localMarksRepository.saveMarks(auth, it) },
        ) { remoteMarksRepository.getMarks(auth, period) }
    }

    override fun getMarksActual(
        auth: AuthScope,
        period: MarksOutput.Period
    ): FlowResource<MarksOutput> {
        return merger.remote(
            save = { localMarksRepository.saveMarks(auth, it) },
        ) { remoteMarksRepository.getMarks(auth, period) }
    }

    override fun getLesson(
        auth: AuthScope,
        period: MarksOutput.Period,
        title: String
    ): FlowResource<LessonOutput> {
        return merger.local(localRequest = {
            localMarksRepository.getLesson(auth, period.period, title)
        })
    }

}