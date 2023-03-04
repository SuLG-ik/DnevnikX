package ru.sulgik.dnevnikx.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import ru.sulgik.dnevnikx.repository.account.room.AccountDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataEntity
import ru.sulgik.dnevnikx.repository.account.room.AccountEntity
import ru.sulgik.dnevnikx.repository.auth.room.AuthDao
import ru.sulgik.dnevnikx.repository.auth.room.AuthEntity
import ru.sulgik.dnevnikx.repository.diary.room.DiaryDao
import ru.sulgik.dnevnikx.repository.diary.room.DiaryDateEntity
import ru.sulgik.dnevnikx.repository.diary.room.DiaryDateLessonEntity
import ru.sulgik.dnevnikx.repository.diary.room.LessonFileEntity
import ru.sulgik.dnevnikx.repository.diary.room.LessonHomeworkEntity
import ru.sulgik.dnevnikx.repository.diary.room.LessonMarkEntity
import ru.sulgik.dnevnikx.repository.finalmarks.room.FinalMarksDao
import ru.sulgik.dnevnikx.repository.finalmarks.room.FinalMarksLessonEntity
import ru.sulgik.dnevnikx.repository.finalmarks.room.FinalMarksLessonMarkEntity
import ru.sulgik.dnevnikx.repository.marks.room.MarksDao
import ru.sulgik.dnevnikx.repository.marks.room.MarksLessonEntity
import ru.sulgik.dnevnikx.repository.marks.room.MarksLessonMarkEntity
import ru.sulgik.dnevnikx.repository.marks.room.MarksPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.HostPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.NestedPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.PeriodDao

@Database(
    entities = [
        AccountEntity::class, AccountDataEntity::class, AuthEntity::class,
        HostPeriodEntity::class, NestedPeriodEntity::class,
        DiaryDateEntity::class, DiaryDateLessonEntity::class, LessonFileEntity::class, LessonHomeworkEntity::class, LessonMarkEntity::class,
        MarksPeriodEntity::class, MarksLessonEntity::class, MarksLessonMarkEntity::class,
        FinalMarksLessonEntity::class, FinalMarksLessonMarkEntity::class
    ],
    version = 3,
    autoMigrations = [AutoMigration(from = 1, to = 2), AutoMigration(from = 2, to = 3)]
)
@TypeConverters(Converters::class)
abstract class DnevnikXDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val authDao: AuthDao
    abstract val accountDataDao: AccountDataDao
    abstract val periodDao: PeriodDao
    abstract val diaryDao: DiaryDao
    abstract val marksDao: MarksDao
    abstract val finalMarksDao: FinalMarksDao

}

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