package ru.sulgik.dnevnikx.platform.android

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toJavaLocalTime
import ru.sulgik.dnevnikx.platform.DatePeriod
import ru.sulgik.dnevnikx.platform.TimeFormatter
import ru.sulgik.dnevnikx.platform.TimePeriod
import java.time.format.DateTimeFormatter

class AndroidTimeFormatter : TimeFormatter {

    private val localDateFormatter = DateTimeFormatter.ofPattern("dd.MM")
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val localDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

    override fun format(value: LocalDate): String {
        return localDateFormatter.format(value.toJavaLocalDate())
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
        return "${format(value.start)}:${format(value.end)}"
    }

}