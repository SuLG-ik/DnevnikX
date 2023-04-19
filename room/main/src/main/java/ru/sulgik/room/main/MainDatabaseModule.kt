package ru.sulgik.room.main

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.account.domain.AccountDao
import ru.sulgik.account.domain.AccountDataDao
import ru.sulgik.diary.domain.DiaryDao
import ru.sulgik.finalmarks.domain.FinalMarksDao
import ru.sulgik.marks.domain.MarksDao
import ru.sulgik.periods.domain.room.PeriodDao
import ru.sulgik.room.main.migrations.migrations
import ru.sulgik.schedule.add.domain.ScheduleClassDao
import ru.sulgik.schedule.list.domain.ScheduleDao


@Module
class MainDatabaseModule {

    @Single
    fun mainDatabase(
        applicationContext: Context,
    ): MainDnevnikXDatabase {
        return Room.databaseBuilder(applicationContext, MainDnevnikXDatabase::class.java, "db")
            .addMigrations(*migrations)
            .build()
    }

    @Single
    fun accountDao(db: MainDnevnikXDatabase): AccountDao {
        return db.accountDao
    }

    @Single
    fun accountDataDao(db: MainDnevnikXDatabase): AccountDataDao {
        return db.accountDataDao
    }

    @Single
    fun periodDao(db: MainDnevnikXDatabase): PeriodDao {
        return db.periodDao
    }

    @Single
    fun diaryDao(db: MainDnevnikXDatabase): DiaryDao {
        return db.diaryDao
    }

    @Single
    fun marksDao(db: MainDnevnikXDatabase): MarksDao {
        return db.marksDao
    }

    @Single
    fun finalMarksDao(db: MainDnevnikXDatabase): FinalMarksDao {
        return db.finalMarksDao
    }

    @Single
    fun scheduleClassDao(db: MainDnevnikXDatabase): ScheduleClassDao {
        return db.scheduleClassDao
    }

    @Single
    fun scheduleDao(db: MainDnevnikXDatabase): ScheduleDao {
        return db.scheduleDao
    }

}