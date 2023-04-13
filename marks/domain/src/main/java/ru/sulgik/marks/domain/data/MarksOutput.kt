package ru.sulgik.marks.domain.data

import kotlinx.datetime.LocalDate
import ru.sulgik.common.platform.DatePeriod

data class MarksOutput(
    val period: Period,
    val lessons: List<Lesson>,
) {

    data class Period(
        val title: String,
        val period: DatePeriod,
    )

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