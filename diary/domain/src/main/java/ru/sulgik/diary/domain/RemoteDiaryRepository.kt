package ru.sulgik.diary.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.diary.domain.data.DiaryOutput

interface RemoteDiaryRepository {

    suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput

}