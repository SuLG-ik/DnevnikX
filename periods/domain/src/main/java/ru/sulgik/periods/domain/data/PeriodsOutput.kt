package ru.sulgik.periods.domain.data

import ru.sulgik.common.platform.DatePeriod

data class GetPeriodsOutput(
    val periods: List<Period>,
) {

    data class Period(
        val title: String,
        val period: DatePeriod,
        val nestedPeriods: List<DatePeriod>,
    )
}
