package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.MarksOutput

interface CachedMarksRepository {

    suspend fun getMarksFast(auth: AuthScope, period: DatePeriod): MarksOutput

    suspend fun getMarksActual(auth: AuthScope, period: DatePeriod): MarksOutput

}