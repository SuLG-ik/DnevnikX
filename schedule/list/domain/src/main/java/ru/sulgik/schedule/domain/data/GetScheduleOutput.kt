package ru.sulgik.schedule.domain.data

import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.TimePeriod

data class GetScheduleOutput(
    val classFullTitle: String,
    val schedule: List<Item>,
) {
    data class Item(
        val date: LocalDate,
        val lessonGroups: List<LessonGroup>,
    )

    data class LessonGroup(
        val number: String,
        val lessons: List<Lesson>,
    )

    data class Lesson(
        val title: String,
        val time: TimePeriod,
        val teacher: String,
        val group: String? = null,
    )

}
