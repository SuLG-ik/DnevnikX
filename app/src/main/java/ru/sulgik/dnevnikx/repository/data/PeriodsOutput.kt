package ru.sulgik.dnevnikx.repository.data

import ru.sulgik.dnevnikx.platform.DatePeriod

data class GetPeriodsOutput(
    val periods: List<Period>,
) {

    data class Period(
        val title: String,
        val period: DatePeriod,
        val nestedPeriods: List<DatePeriod>,
    )
}
