package ru.sulgik.dnevnikx.repository.data

import kotlinx.datetime.LocalDate

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
        val marks: List<Mark>,
    )

    data class Homework(
        val text: String,
    )
    data class Mark(
        val mark: String,
        val value: Int,
    )
}
