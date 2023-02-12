package ru.sulgik.dnevnikx.repository.data

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class GetPeriodsOutput(
    val periods: List<Period>,
) {

    data class Period(
        val title: String,
        val period: DatePeriod,
        val nestedPeriods: List<DatePeriod>,
    )
}

open class ComparableRange<T : Comparable<T>>(
    override val start: T,
    override val endInclusive: T,
) : ClosedRange<T> {

    override fun equals(other: Any?): Boolean {
        return other is ComparableRange<*> && (isEmpty() && other.isEmpty() ||
                start == other.start && endInclusive == other.endInclusive)
    }

    override fun hashCode(): Int {
        return if (isEmpty()) -1 else 31 * start.hashCode() + endInclusive.hashCode()
    }

    override fun toString(): String = "$start..$endInclusive"
}

class DatePeriod(
    override val start: LocalDate,
    val end: LocalDate,
) : ComparableRange<LocalDate>(start, end)


data class TimePeriod(
    val start: LocalTime,
    val end: LocalTime,
)