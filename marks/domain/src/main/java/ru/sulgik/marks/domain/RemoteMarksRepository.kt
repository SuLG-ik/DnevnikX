package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.MarksOutput

interface RemoteMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput

}
