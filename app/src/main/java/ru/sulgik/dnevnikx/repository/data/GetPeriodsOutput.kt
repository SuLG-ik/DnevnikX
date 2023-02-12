package ru.sulgik.dnevnikx.repository.data

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
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
