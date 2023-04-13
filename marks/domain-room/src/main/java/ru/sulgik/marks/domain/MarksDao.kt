package ru.sulgik.marks.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.datetime.LocalDate
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod

@Dao
interface MarksDao {

    @Query("SELECT * FROM MarksPeriodEntity WHERE accountId = :accountId AND start = :start AND `end` = :end ")
    @Transaction
    suspend fun getMarks(
        accountId: String,
        start: LocalDate,
        end: LocalDate,
    ): MarksPeriodWithLessons?

    @Query("DELETE FROM MarksPeriodEntity WHERE accountId = :accountId AND start = :start AND `end` = :end ")
    @Transaction
    suspend fun deleteMarks(accountId: String, start: LocalDate, end: LocalDate)

    @Insert
    suspend fun saveMarksPeriod(period: MarksPeriodEntity): Long

    @Insert
    suspend fun saveMarksLessons(lessons: List<MarksLessonEntity>): List<Long>

    @Insert
    suspend fun saveMarksLessonMarks(lessons: List<MarksLessonMarkEntity>)

    @Transaction
    @Query("SELECT * FROM MarksLessonEntity WHERE periodId = :periodId AND title = :title")
    suspend fun getLesson(periodId: Long, title: String): MarksLessonWithMarks

    @Query("SELECT * FROM MarksPeriodEntity WHERE accountId = :accountId AND start = :start AND `end` = :end")
    suspend fun getPeriod(accountId: String, start: LocalDate, end: LocalDate): MarksPeriodEntity

    @Transaction
    suspend fun getMarksPeriodWithLesson(
        authScope: AuthScope,
        period: DatePeriod,
        title: String
    ): Pair<MarksPeriodEntity, MarksPeriodWithLesson> {
        val period = getPeriod(authScope.id, period.start, period.end)
        val lesson = getLesson(period.id, title)
        return period to MarksPeriodWithLesson(period, lesson)
    }

    @Transaction
    suspend fun saveMarks(
        period: MarksPeriodEntity,
        lessons: List<MarksLessonWithMarks>,
    ) {
        deleteMarks(period.accountId, period.start, period.end)
        val periodId = saveMarksPeriod(period)
        val lessonsIds = saveMarksLessons(lessons.map {
            it.lesson.periodId = periodId
            it.lesson
        })
        val marks = lessons.flatMapIndexed { index, marksLessonWithMarks ->
            val lessonId = lessonsIds[index]
            marksLessonWithMarks.marks.map {
                it.lessonId = lessonId
                it
            }
        }
        saveMarksLessonMarks(marks)
    }


}