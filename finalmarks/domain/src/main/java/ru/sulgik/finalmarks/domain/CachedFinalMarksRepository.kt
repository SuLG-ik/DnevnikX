package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput

interface CachedFinalMarksRepository {


    suspend fun getFinalMarksFast(auth: AuthScope): FinalMarksOutput

    suspend fun getFinalMarksActual(auth: AuthScope): FinalMarksOutput

}