package ru.sulgik.marksupdates.domain.data

import kotlinx.datetime.LocalDate


data class MarksUpdatesOutput(
    val latest: List<MarkUpdate>,
    val old: List<MarkUpdate>,
) {

    data class MarkUpdate(
        val lesson: Lesson,
        val currentMark: Mark,
        val previousMark: Mark?,
    )

    data class Lesson(
        val name: String,
        val date: LocalDate,
    )

    data class Mark(
        val mark: String,
        val value: Int,
    )

}