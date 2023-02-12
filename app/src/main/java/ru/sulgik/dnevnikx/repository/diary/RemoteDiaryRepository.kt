package ru.sulgik.dnevnikx.repository.diary

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.DiaryOutput
import ru.sulgik.dnevnikx.platform.DatePeriod

interface RemoteDiaryRepository {

    suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput

}