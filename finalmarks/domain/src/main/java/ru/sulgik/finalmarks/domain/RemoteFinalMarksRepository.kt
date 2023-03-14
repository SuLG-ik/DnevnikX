package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput


interface RemoteFinalMarksRepository {

    suspend fun getFinalMarks(auth: AuthScope): FinalMarksOutput

}