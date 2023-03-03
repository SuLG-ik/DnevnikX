package ru.sulgik.dnevnikx.repository.marks

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput

interface LocalMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput?

    suspend fun saveMarks(auth: AuthScope, period: DatePeriod, marks: MarksOutput)

}
