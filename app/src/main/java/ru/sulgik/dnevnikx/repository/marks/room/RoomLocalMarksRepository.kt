package ru.sulgik.dnevnikx.repository.marks.room

import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.data.AuthScope
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.repository.data.MarksOutput
import ru.sulgik.dnevnikx.repository.marks.LocalMarksRepository

@Single
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