package ru.sulgik.diary.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput

interface CachedDiaryRepository {

    suspend fun getDiaryFast(auth: AuthScope, period: DatePeriod): DiaryOutput

    suspend fun getDiaryActual(auth: AuthScope, period: DatePeriod): DiaryOutput

}