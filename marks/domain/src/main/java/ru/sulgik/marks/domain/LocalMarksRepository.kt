package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.LessonOutput
import ru.sulgik.marks.domain.data.MarksOutput

interface LocalMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput?

    suspend fun saveMarks(auth: AuthScope, marks: MarksOutput)

    suspend fun getLesson(auth: AuthScope, period: DatePeriod, title: String): LessonOutput
}
