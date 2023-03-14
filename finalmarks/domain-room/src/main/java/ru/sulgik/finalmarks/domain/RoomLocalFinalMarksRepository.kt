package ru.sulgik.finalmarks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.finalmarks.domain.data.FinalMarksOutput

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