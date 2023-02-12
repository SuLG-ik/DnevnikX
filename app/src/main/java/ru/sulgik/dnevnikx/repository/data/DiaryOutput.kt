package ru.sulgik.dnevnikx.repository.data

import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.platform.TimePeriod

data class DiaryOutput(
    val diary: List<Item>,
) {
    data class Item(
        val date: LocalDate,
        val lessons: List<Lesson>,
    )

    data class Lesson(
        val number: String,
        val title: String,
        val time: TimePeriod,
        val homework: List<Homework>,
        val files: List<File>,
        val marks: List<Mark>,
    )

    data class Homework(
        val text: String,
    )

    data class File(
        val name: String,
        val url: String,
    )

    data class Mark(
        val mark: String,
        val value: Int,
    )
}
