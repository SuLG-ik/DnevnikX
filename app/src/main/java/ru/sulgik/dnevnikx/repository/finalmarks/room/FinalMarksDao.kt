package ru.sulgik.dnevnikx.repository.finalmarks.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FinalMarksDao {


    @Query("DELETE FROM FinalMarksLessonEntity WHERE accountId = :accountId")
    suspend fun clearFinalMarks(accountId: String)

    @Insert
    suspend fun saveLessonMarks(marks: List<FinalMarksLessonMarkEntity>)

    @Insert
    suspend fun saveLessons(lessons: List<FinalMarksLessonEntity>): List<Long>

    @Transaction
    @Query("SELECT * FROM FinalMarksLessonEntity WHERE accountId = :accountId")
    suspend fun getLessons(accountId: String): List<FinalMarksLessonWithMarks>

    @Transaction
    suspend fun saveFinalMarksLessons(accountId: String, lessons: List<FinalMarksLessonWithMarks>) {
        clearFinalMarks(accountId)
        val lessonIds = saveLessons(lessons.map { it.lesson })
        saveLessonMarks(
            lessons.flatMapIndexed { index, value ->
                val lessonId = lessonIds[index]
                value.marks.map {
                    it.lessonId = lessonId
                    it
                }
            }
        )
    }

}