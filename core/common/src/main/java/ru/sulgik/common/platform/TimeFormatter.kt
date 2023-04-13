package ru.sulgik.common.platform

import androidx.compose.runtime.compositionLocalOf
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toKotlinLocalDate

interface TimeFormatter {

    fun format(value: LocalDate): String

    fun formatLiteral(value: LocalDate): String

    fun format(value: LocalTime): String

    fun format(value: LocalDateTime): String

    fun format(value: DatePeriod): String

    fun format(value: TimePeriod): String

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

data class DatePeriod(
    override val start: LocalDate,
    val end: LocalDate,
) : ComparableRange<LocalDate>(start, end) {

    fun toParcelable(): DatePeriodParcelable {
        return DatePeriodParcelable(start.toJavaLocalDate(), end.toJavaLocalDate())
    }
}

@Parcelize
data class DatePeriodParcelable(
    val start: java.time.LocalDate,
    val end: java.time.LocalDate
) : Parcelable {
    fun toDatePeriod(): DatePeriod {
        return DatePeriod(start.toKotlinLocalDate(), end.toKotlinLocalDate())
    }
}


data class TimePeriod(
    val start: LocalTime,
    val end: LocalTime,
)

val LocalTimeFormatter =
    compositionLocalOf<TimeFormatter> { error("LocalTimeFormatter is not provided") }