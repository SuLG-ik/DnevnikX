package ru.sulgik.dnevnikx.repository.finalmarks.room

import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.repository.data.FinalMarksOutput
import ru.sulgik.dnevnikx.repository.finalmarks.LocalFinalMarksRepository

@Single
class RoomLocalFinalMarksRepository(
    private val finalMarksDao: FinalMarksDao,
) : LocalFinalMarksRepository {

    override suspend fun getFinalMarks(auth: AuthScope): FinalMarksOutput? {
        val finalMarks = finalMarksDao.getLessons(auth.id).ifEmpty { return null }
        return FinalMarksOutput(
            finalMarks.map { lesson ->
                FinalMarksOutput.Lesson(
                    lesson.lesson.title,
                    lesson.marks.map { mark ->
                        FinalMarksOutput.Mark(
                            mark.mark,
                            mark.value,
                            mark.period
                        )
                    }
                )
            }
        )
    }

    override suspend fun saveFinalMarks(auth: AuthScope, marks: FinalMarksOutput) {
        finalMarksDao.saveFinalMarksLessons(auth.id, marks.lessons.map { lesson ->
            FinalMarksLessonWithMarks(
                lesson = FinalMarksLessonEntity(
                    accountId = auth.id,
                    title = lesson.title,
                ),
                marks = lesson.marks.map { mark ->
                    FinalMarksLessonMarkEntity(
                        mark.mark,
                        mark.value,
                        mark.period,
                    )
                }
            )
        })
    }

}