package ru.sulgik.common.platform

import androidx.compose.runtime.compositionLocalOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DateTimeUnit.Companion.WEEK
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.sulgik.common.DateProvider

class AndroidDateProvider : DateProvider {

    override fun currentDateTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }

    override fun currentDate(): LocalDate {
        return currentDateTime().date
    }

    override fun currentWeek(): DatePeriod {
        val date = currentDate()
        val start = date.minus((date.dayOfWeek.value - 1).toLong(), DateTimeUnit.DAY)
        return DatePeriod(start, start.plus(6, DateTimeUnit.DAY))
    }

    override fun DatePeriod.plus(weeks: Int): DatePeriod {
        return DatePeriod(start.plus(weeks, WEEK), end.plus(weeks, WEEK))
    }

    override fun DatePeriod.minus(weeks: Int): DatePeriod {
        return plus(-weeks)
    }

}

val LocalDateProvider =
    compositionLocalOf<DateProvider> { error("LocalTimeFormatter is not provided") }