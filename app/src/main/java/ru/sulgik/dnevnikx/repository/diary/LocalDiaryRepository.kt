package ru.sulgik.dnevnikx.repository.diary

import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput

interface LocalDiaryRepository {

    suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput?

    suspend fun saveDiary(auth: AuthScope, diary: DiaryOutput)

}