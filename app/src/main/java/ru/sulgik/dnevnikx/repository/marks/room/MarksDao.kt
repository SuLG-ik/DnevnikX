package ru.sulgik.dnevnikx.repository.marks.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.platform.DatePeriod

@Dao
interface MarksDao {

    @Query("SELECT * FROM MarksPeriodEntity WHERE accountId = :accountId AND start = :start AND `end` = :end ")
    @Transaction
    suspend fun getMarks(
        accountId: String,
        start: LocalDate,
        end: LocalDate,
    ): MarksPeriodWithLesson?

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
    suspend fun saveMarks(
        accountId: String,
        period: DatePeriod,
        lessons: List<MarksLessonWithMarks>,
    ) {
        deleteMarks(accountId, period.start, period.end)
        val periodId = saveMarksPeriod(MarksPeriodEntity(accountId, period.start, period.end))
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