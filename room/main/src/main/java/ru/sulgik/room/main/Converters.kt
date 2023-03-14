package ru.sulgik.room.main

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class Converters {
    @TypeConverter
    fun localDateFromTimestamp(value: Int?): LocalDate? {
        return value?.let { LocalDate.fromEpochDays(it) }
    }

    @TypeConverter
    fun localDateToTimestamp(date: LocalDate?): Int? {
        return date?.toEpochDays()
    }

    @TypeConverter
    fun localTimeFromTimestamp(value: Int?): LocalTime? {
        return value?.let { LocalTime.fromMillisecondOfDay(it) }
    }

    @TypeConverter
    fun localTimeToTimestamp(date: LocalTime?): Int? {
        return date?.toMillisecondOfDay()
    }
}