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

    override suspend fun saveDiary(auth: AuthScope, diary: DiaryOutput) {
        val dates = mutableListOf<DiaryDateEntity>()
        val lessons = mutableListOf<List<DiaryDateLessonWithMarksHomeworkFiles>>()
        diary.diary.forEach { diaryDate ->
            dates.add(
                DiaryDateEntity(
                    alert = diaryDate.alert?.let { alert ->
                        DiaryDateAlert(alert.message, alert.alert)
                    },
                    date = diaryDate.date,
                    accountId = auth.id,
                )
            )
            lessons.add(diaryDate.lessons.map { lesson ->
                DiaryDateLessonWithMarksHomeworkFiles(
                    lesson = DiaryDateLessonEntity(
                        lesson.number,
                        lesson.title,
                        lesson.time,
                    ),
                    homeworks = lesson.homework.map { homework ->
                        LessonHomeworkEntity(homework.text)
                    },
                    marks = lesson.marks.map { mark ->
                        LessonMarkEntity(
                            mark.value,
                            mark.mark
                        )
                    },
                    files = lesson.files.map { file ->
                        LessonFileEntity(
                            file.name,
                            file.url
                        )
                    }
                )
            })
        }
        diaryDao.saveDiary(
            accountId = auth.id,
            date = dates,
            lessons = lessons,
        )
    }

    override suspend fun getDiary(auth: AuthScope, period: DatePeriod): DiaryOutput? {
        val diary = diaryDao.getDiary(auth.id, period.start, period.end)
        if (diary.isEmpty())
            return null
        return DiaryOutput(
            diary.map { diaryDate ->
                DiaryOutput.Item(
                    diaryDate.diaryDate.date,
                    alert = diaryDate.diaryDate.alert?.let { alert ->
                        DiaryOutput.Alert(
                            alert = alert.alert, message = alert.message
                        )
                    },
                    lessons = diaryDate.lessons.map { lesson ->
                        DiaryOutput.Lesson(
                            number = lesson.lesson.number,
                            title = lesson.lesson.title,
                            time = lesson.lesson.time,
                            homework = lesson.homeworks.map {
                                DiaryOutput.Homework(
                                    it.text,
                                )
                            },
                            files = lesson.files.map { file ->
                                DiaryOutput.File(
                                    file.name,
                                    file.url,
                                )
                            },
                            marks = lesson.marks.map { mark ->
                                DiaryOutput.Mark(
                                    mark = mark.mark,
                                    value = mark.value
                                )
                            }
                        )
                    }
                )
            }
        )
    }

}