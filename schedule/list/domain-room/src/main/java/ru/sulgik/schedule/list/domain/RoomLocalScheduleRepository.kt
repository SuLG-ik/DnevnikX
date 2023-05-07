package ru.sulgik.schedule.list.domain

import ru.sulgik.auth.core.AuthScope
import ru.sulgik.schedule.domain.LocalScheduleRepository
import ru.sulgik.schedule.domain.data.GetScheduleOutput

class RoomLocalScheduleRepository(
    private val dao: ScheduleDao,
) : LocalScheduleRepository {

    override suspend fun getSchedule(auth: AuthScope, classGroup: String): GetScheduleOutput? {
        val (number, group) = classGroup.separateNumberAndGroup()
        val response = dao.getSchedules(auth, number, group)?.ifEmpty { null } ?: return null
        return GetScheduleOutput(
            classFullTitle = classGroup,
            schedule = response.groupBy { it.date }
                .map {
                    GetScheduleOutput.Item(
                        it.key,
                        it.value.groupBy { lessonDate -> lessonDate.number }.map { lessonGroup ->
                            GetScheduleOutput.LessonGroup(
                                lessonGroup.key,
                                lessonGroup.value.map { lesson ->
                                    GetScheduleOutput.Lesson(
                                        title = lesson.title,
                                        time = lesson.time,
                                        teacher = lesson.teacherName,
                                        group = lesson.group
                                    )
                                }
                            )
                        }
                    )
                }
        )
    }

    override suspend fun saveSchedule(auth: AuthScope, data: GetScheduleOutput) {
        val (number, group) = data.classFullTitle.separateNumberAndGroup()
        dao.saveSchedules(auth, number, group, data.schedule)
    }


}

private fun String.separateNumberAndGroup(): Pair<String, String> {
    val number = StringBuilder()
    val group = StringBuilder()
    var isGroup = false
    forEach {
        when {
            isGroup -> group.append(it)
            !it.isDigit() -> {
                group.append(it)
                isGroup = true
            }

            else -> number.append(it)
        }
    }
    return number.toString() to group.toString()
}
