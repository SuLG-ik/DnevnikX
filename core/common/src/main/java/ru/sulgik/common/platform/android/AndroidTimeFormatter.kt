package ru.sulgik.common.platform.android

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import ru.sulgik.common.platform.DatePeriod
import ru.sulgik.common.platform.TimeFormatter
import ru.sulgik.common.platform.TimePeriod
import java.time.DayOfWeek
import java.time.Month
import java.time.format.DateTimeFormatter

class AndroidTimeFormatter : TimeFormatter {

    private val localDateFormatter = DateTimeFormatter.ofPattern("dd.MM")
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val localDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

    override fun format(value: LocalDate): String {
        return localDateFormatter.format(value.toJavaLocalDate())
    }

    override fun formatLiteral(value: LocalDate): String {
        return "${value.formatWeek()}, ${value.dayOfMonth} ${value.formatMonth()}"
    }

    private fun LocalDate.formatMonth(): String {
        return when (month) {
            Month.JANUARY -> "января"
            Month.FEBRUARY -> "февраля"
            Month.MARCH -> "марта"
            Month.APRIL -> "апреля"
            Month.MAY -> "мая"
            Month.JUNE -> "июня"
            Month.JULY -> "июля"
            Month.AUGUST -> "августа"
            Month.SEPTEMBER -> "сентября"
            Month.OCTOBER -> "октября"
            Month.NOVEMBER -> "ноября"
            Month.DECEMBER -> "декабря"
        }
    }

    private fun LocalDate.formatWeek(): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Понедельник"
            DayOfWeek.TUESDAY -> "Вторник"
            DayOfWeek.WEDNESDAY -> "Среда"
            DayOfWeek.THURSDAY -> "Четверг"
            DayOfWeek.FRIDAY -> "Пятница"
            DayOfWeek.SATURDAY -> "Суббота"
            DayOfWeek.SUNDAY -> "Воскресенье"
        }
    }

    override fun format(value: LocalTime): String {
        return localTimeFormatter.format(value.toJavaLocalTime())
    }

    override fun format(value: LocalDateTime): String {
        return localDateTimeFormatter.format(value.toJavaLocalDateTime())
    }

    override fun format(value: DatePeriod): String {
       return "${format(value.start)} - ${format(value.end)}"
    }

    override fun format(value: TimePeriod): String {
        return "${format(value.start)} - ${format(value.end)}"
    }

}