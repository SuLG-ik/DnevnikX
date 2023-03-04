package ru.sulgik.dnevnikx.repository.finalmarks

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.FinalMarksOutput

interface LocalFinalMarksRepository {

    suspend fun getFinalMarks(auth: AuthScope): FinalMarksOutput?

    suspend fun saveFinalMarks(auth: AuthScope, marks: FinalMarksOutput)

}