package ru.sulgik.dnevnikx.repository.diary

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput

interface CachedDiaryRepository {

    suspend fun getDiaryFast(auth: AuthScope, period: DatePeriod): DiaryOutput

    suspend fun getDiaryActual(auth: AuthScope, period: DatePeriod): DiaryOutput

}