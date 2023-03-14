package ru.sulgik.diary.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput

interface LocalDiaryRepository {

    suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput?

    suspend fun saveDiary(auth: AuthScope, diary: DiaryOutput)

}