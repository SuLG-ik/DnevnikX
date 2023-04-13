package ru.sulgik.diary.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput
import ru.sulgik.kacher.core.FlowResource

interface CachedDiaryRepository {

    fun getDiaryActual(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput>

    fun getDiaryOld(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput>

    fun getDiary(auth: AuthScope, period: DatePeriod): FlowResource<DiaryOutput>

}