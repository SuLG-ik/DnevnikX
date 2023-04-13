package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.marks.domain.data.LessonOutput
import ru.sulgik.marks.domain.data.MarksOutput

interface CachedMarksRepository {

    fun getMarks(auth: AuthScope, period: MarksOutput.Period): FlowResource<MarksOutput>

    fun getMarksActual(auth: AuthScope, period: MarksOutput.Period): FlowResource<MarksOutput>

    fun getMarksOld(auth: AuthScope, period: MarksOutput.Period): FlowResource<MarksOutput>

    fun getLesson(
        auth: AuthScope,
        period: MarksOutput.Period,
        title: String
    ): FlowResource<LessonOutput>
}