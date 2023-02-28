package ru.sulgik.dnevnikx.repository.diary.room

import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.DiaryOutput
import ru.sulgik.dnevnikx.repository.diary.LocalDiaryRepository

@Single
class RoomLocalDiaryRepository(
    private val diaryDao: DiaryDao,
) : LocalDiaryRepository {

    override suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput? {
        val diary = diaryDao.getDiary(auth.id, period.start, period.end)
        TODO()
    }

}