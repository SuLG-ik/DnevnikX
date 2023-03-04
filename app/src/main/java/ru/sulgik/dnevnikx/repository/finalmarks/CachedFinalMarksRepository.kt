package ru.sulgik.dnevnikx.repository.finalmarks

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.FinalMarksOutput

interface CachedFinalMarksRepository {


    suspend fun getFinalMarksFast(auth: AuthScope): FinalMarksOutput

    suspend fun getFinalMarksActual(auth: AuthScope): FinalMarksOutput

}