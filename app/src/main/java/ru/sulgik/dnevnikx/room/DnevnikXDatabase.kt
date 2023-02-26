package ru.sulgik.dnevnikx.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.datetime.LocalDate
import ru.sulgik.dnevnikx.repository.account.room.AccountDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataEntity
import ru.sulgik.dnevnikx.repository.account.room.AccountEntity
import ru.sulgik.dnevnikx.repository.auth.room.AuthDao
import ru.sulgik.dnevnikx.repository.auth.room.AuthEntity
import ru.sulgik.dnevnikx.repository.periods.room.HostPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.NestedPeriodEntity
import ru.sulgik.dnevnikx.repository.periods.room.PeriodDao

@Database(
    entities = [AccountEntity::class, AccountDataEntity::class, AuthEntity::class, HostPeriodEntity::class, NestedPeriodEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(1, 2)]
)
@TypeConverters(Converters::class)
abstract class DnevnikXDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val authDao: AuthDao
    abstract val accountDataDao: AccountDataDao
    abstract val periodDao: PeriodDao

}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Int?): LocalDate? {
        return value?.let { LocalDate.fromEpochDays(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Int? {
        return date?.toEpochDays()
    }
}