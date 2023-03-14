package ru.sulgik.marks.domain.data

import kotlinx.datetime.LocalDate

data class MarksOutput(
    val lessons: List<Lesson>,
) {
    data class Lesson(
        val title: String,
        val average: String,
        val averageValue: Int,
        val marks: List<Mark>,
    )

    data class Mark(
        val mark: String,
        val value: Int,
        val date: LocalDate,
        val message: String?,
    )
}