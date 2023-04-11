package ru.sulgik.finalmarks.domain

import kotlinx.coroutines.flow.Flow
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput
import ru.sulgik.kacher.core.Resource

interface CachedFinalMarksRepository {

    fun getFinalMarksActual(auth: AuthScope): Flow<Resource<FinalMarksOutput>>

    fun getFinalMarks(auth: AuthScope): Flow<Resource<FinalMarksOutput>>

}