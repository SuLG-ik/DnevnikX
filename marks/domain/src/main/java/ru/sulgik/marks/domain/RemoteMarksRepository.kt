package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.marks.domain.data.MarksOutput

interface RemoteMarksRepository {

    suspend fun getMarks(auth: AuthScope, period: MarksOutput.Period): MarksOutput

}
