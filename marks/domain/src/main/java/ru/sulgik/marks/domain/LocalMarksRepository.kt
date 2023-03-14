package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.MarksOutput

interface LocalMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput?

    suspend fun saveMarks(auth: AuthScope, period: DatePeriod, marks: MarksOutput)

}
