package ru.sulgik.common

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import ru.sulgik.common.platform.DatePeriod

interface DateProvider {

    fun currentDateTime(): LocalDateTime

    fun currentWeek(): DatePeriod

    fun currentDate(): LocalDate

    operator fun DatePeriod.plus(weeks: Int): DatePeriod

    operator fun DatePeriod.minus(weeks: Int): DatePeriod

}