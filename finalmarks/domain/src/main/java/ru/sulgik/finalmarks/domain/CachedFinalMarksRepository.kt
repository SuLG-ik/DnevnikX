package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput
import ru.sulgik.kacher.core.FlowResource

interface CachedFinalMarksRepository {

    fun getFinalMarksActual(auth: AuthScope): FlowResource<FinalMarksOutput>

    fun getFinalMarks(auth: AuthScope): FlowResource<FinalMarksOutput>

}