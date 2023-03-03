package ru.sulgik.dnevnikx.repository.marks

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput

interface CachedMarksRepository {

    suspend fun getMarksFast(auth: AuthScope, period: DatePeriod): MarksOutput

    suspend fun getMarksActual(auth: AuthScope, period: DatePeriod): MarksOutput

}