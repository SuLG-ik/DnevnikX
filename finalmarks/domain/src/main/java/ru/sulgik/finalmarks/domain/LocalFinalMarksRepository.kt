package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput

interface LocalFinalMarksRepository {

    suspend fun getFinalMarks(auth: AuthScope): FinalMarksOutput?

    suspend fun saveFinalMarks(auth: AuthScope, marks: FinalMarksOutput)

}