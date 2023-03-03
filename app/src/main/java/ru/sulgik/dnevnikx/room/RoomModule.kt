package ru.sulgik.dnevnikx.room

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.dnevnikx.repository.account.room.AccountDao
import ru.sulgik.dnevnikx.repository.account.room.AccountDataDao
import ru.sulgik.dnevnikx.repository.auth.room.AuthDao
import ru.sulgik.dnevnikx.repository.diary.room.DiaryDao
import ru.sulgik.dnevnikx.repository.marks.room.MarksDao
import ru.sulgik.dnevnikx.repository.periods.room.PeriodDao
import ru.sulgik.dnevnikx.room.migrations.migrations

@Module
class RoomModule {

    @Single
    fun bindsDatabase(
        context: Context,
    ): DnevnikXDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = DnevnikXDatabase::class.java,
            name = "dnevnikx",
        )
            .addMigrations(*migrations)
            .build()
    }

    @Single
    fun bindsAccountDao(database: DnevnikXDatabase): AccountDao {
        return database.accountDao
    }

    @Single
    fun bindsAccountDataDao(database: DnevnikXDatabase): AccountDataDao {
        return database.accountDataDao
    }

    @Single
    fun bindsAuthDao(database: DnevnikXDatabase): AuthDao {
        return database.authDao
    }

    @Single
    fun bindsPeriodDao(database: DnevnikXDatabase): PeriodDao {
        return database.periodDao
    }

    @Single
    fun bindsDiaryDao(database: DnevnikXDatabase): DiaryDao {
        return database.diaryDao
    }


    @Single
    fun bindsMarksDao(database: DnevnikXDatabase): MarksDao {
        return database.marksDao
    }

}