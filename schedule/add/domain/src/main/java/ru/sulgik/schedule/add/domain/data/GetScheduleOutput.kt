package ru.sulgik.schedule.add.domain.data

data class GetScheduleClassesOutput(
    val classes: List<Class>,
) {
    data class Class(
        val fullTitle: String,
        val number: String,
        val group: String,
    )

}
