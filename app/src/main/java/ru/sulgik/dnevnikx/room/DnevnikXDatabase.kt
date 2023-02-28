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
import ru.sulgik.dnevnikx.repository.periods.room.HostPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.NestedPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.PeriodDao

@Database(
    entities = [
        AccountEntity::class, AccountDataEntity::class, AuthEntity::class,
        HostPeriodEntity::class, NestedPeriodEntity::class,
        DiaryDateEntity::class, DiaryDateLessonEntity::class, LessonFileEntity::class, LessonHomeworkEntity::class, LessonMarkEntity::class,
    ],
    version = 4,
    autoMigrations = [AutoMigration(1, 2), AutoMigration(2, 3), AutoMigration(3, 4)]
)
@TypeConverters(Converters::class)
abstract class DnevnikXDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val authDao: AuthDao
    abstract val accountDataDao: AccountDataDao
    abstract val periodDao: PeriodDao
    abstract val diaryDao: DiaryDao

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