package ru.sulgik.diary.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.datetime.LocalDate

@Dao
interface DiaryDao {

    @Transaction
    @Query("SELECT * FROM DiaryDateEntity WHERE accountId = :accountId AND date BETWEEN :start AND :end")
    fun getDiary(accountId: String, start: LocalDate, end: LocalDate): List<DiaryDateWithLessons>

    @Transaction
    @Query("DELETE FROM DiaryDateEntity WHERE accountId = :accountId AND date IN (:dates)")
    fun deleteDiary(accountId: String, dates: List<LocalDate>)

    @Insert
    fun saveDiary(dates: List<DiaryDateEntity>): List<Long>

    @Insert
    fun saveLessons(lessons: List<DiaryDateLessonEntity>): List<Long>

    @Insert
    fun saveHomework(lessons: List<LessonHomeworkEntity>)

    @Insert
    fun saveMarks(lessons: List<LessonMarkEntity>)

    @Insert
    fun saveFiles(lessons: List<LessonFileEntity>)

    @Transaction
    fun saveDiary(
        accountId: String,
        date: List<DiaryDateEntity>,
        lessons: List<List<DiaryDateLessonWithMarksHomeworkFiles>>,
    ) {
        deleteDiary(accountId, date.map { it.date })
        val datesIndexes = saveDiary(date)
        val lessonIds = saveLessons(lessons.flatMapIndexed { index, it ->
            it.map { dateLessonWithMarksHomeworkFiles ->
                dateLessonWithMarksHomeworkFiles.lesson.diaryDateId = datesIndexes[index]
                dateLessonWithMarksHomeworkFiles.lesson
            }
        })
        val flattenLessons = lessons.flatten()
        val marks = mutableListOf<LessonMarkEntity>()
        val homework = mutableListOf<LessonHomeworkEntity>()
        val files = mutableListOf<LessonFileEntity>()
        flattenLessons.forEachIndexed { index, item ->
            val lessonId = lessonIds[index]
            marks.addAll(item.marks.onEach { it.lessonId = lessonId })
            homework.addAll(item.homeworks.onEach { it.lessonId = lessonId })
            files.addAll(item.files.onEach { it.lessonId = lessonId })
        }
        saveMarks(marks)
        saveHomework(homework)
        saveFiles(files)
    }

}