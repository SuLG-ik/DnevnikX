package ru.sulgik.finalmarks.domain.data

class FinalMarksOutput(
    val lessons: List<Lesson>,
) {

    class Lesson(
        val title: String,
        val marks: List<Mark>,
    )

    class Mark(
        val mark: String,
        val value: Int,
        val period: String,
    )

}