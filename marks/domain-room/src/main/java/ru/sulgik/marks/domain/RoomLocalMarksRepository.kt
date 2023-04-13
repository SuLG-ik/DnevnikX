package ru.sulgik.marks.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.marks.domain.data.LessonOutput
import ru.sulgik.marks.domain.data.MarksOutput

class RoomLocalMarksRepository(
    private val marksDao: MarksDao,
) : LocalMarksRepository {

    override suspend fun getMarks(auth: AuthScope, period: DatePeriod): MarksOutput? {
        val lessons = marksDao.getMarks(auth.id, period.start, period.end) ?: return null
        return MarksOutput(
            period = MarksOutput.Period(
                title = lessons.period.title,
                period = DatePeriod(lessons.period.start, lessons.period.end),
            ),
            lessons = lessons.lessons.map { lesson ->
                MarksOutput.Lesson(
                    title = lesson.lesson.title,
                    average = lesson.lesson.average,
                    averageValue = lesson.lesson.averageValue,
                    marks = lesson.marks.map { mark ->
                        MarksOutput.Mark(
                            mark = mark.mark,
                            value = mark.value,
                            date = mark.date,
                            message = mark.message,
                        )
                    }
                )
            }
        )
    }

    override suspend fun saveMarks(auth: AuthScope, marks: MarksOutput) {
        marksDao.saveMarks(
            MarksPeriodEntity(
                accountId = auth.id,
                title = marks.period.title,
                start = marks.period.period.start,
                end = marks.period.period.end
            ), marks.lessons.map { lesson ->
                MarksLessonWithMarks(
                    MarksLessonEntity(
                        title = lesson.title,
                        average = lesson.average,
                        averageValue = lesson.averageValue,
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

    override suspend fun getLesson(
        auth: AuthScope,
        period: DatePeriod,
        title: String
    ): LessonOutput {
        val data =
            marksDao.getMarksPeriodWithLesson(auth, period, title)

        return LessonOutput(
            period = LessonOutput.Period(
                title = data.first.title,
                period = DatePeriod(data.first.start, data.first.end)
            ),
            lesson = data.second.lesson.let { lesson ->
                LessonOutput.Lesson(
                    title = lesson.lesson.title,
                    average = lesson.lesson.average,
                    averageValue = lesson.lesson.averageValue,
                    marks = lesson.marks.map { mark ->
                        LessonOutput.Mark(
                            mark = mark.mark,
                            value = mark.value,
                            date = mark.date,
                            message = mark.message
                        )
                    }
                )
            }
        )
    }

}