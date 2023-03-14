package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.marks.room.MarksDao
import ru.sulgik.marks.domain.data.MarksOutput

class RoomLocalMarksRepository(
    private val marksDao: MarksDao,
) : LocalMarksRepository {

    override suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput? {
        val lessons = marksDao.getMarks(auth.id, period.start, period.end) ?: return null
        return MarksOutput(
            lessons = lessons.lessons.map { lesson ->
                MarksOutput.Lesson(
                    lesson.lesson.title,
                    lesson.lesson.average,
                    lesson.lesson.averageValue,
                    lesson.marks.map { mark ->
                        MarksOutput.Mark(
                            mark.mark,
                            mark.value,
                            mark.date,
                            mark.message,
                        )
                    }
                )
            }
        )
    }

    override suspend fun saveMarks(auth: AuthScope, period: DatePeriod, marks: MarksOutput) {
        marksDao.saveMarks(auth.id, period, marks.lessons.map { lesson ->
            MarksLessonWithMarks(
                MarksLessonEntity(
                    lesson.title,
                    lesson.average,
                    lesson.averageValue
                ),
                lesson.marks.map { mark ->
                    MarksLessonMarkEntity(
                        mark = mark.mark,
                        value = mark.value,
                        date = mark.date,
                        message = mark.message,
                    )
                }
            )
        })
    }

}