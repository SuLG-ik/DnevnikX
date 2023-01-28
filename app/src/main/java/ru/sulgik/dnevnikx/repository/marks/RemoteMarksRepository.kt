package ru.sulgik.dnevnikx.repository.marks

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput

interface RemoteMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput

}
