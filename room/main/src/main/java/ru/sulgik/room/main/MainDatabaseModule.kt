package ru.sulgik.room.main

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import ru.sulgik.account.domain.AccountDao
import ru.sulgik.account.domain.AccountDataDao
import ru.sulgik.diary.domain.DiaryDao
import ru.sulgik.dnevnikx.repository.marks.room.MarksDao
import ru.sulgik.finalmarks.domain.FinalMarksDao
import ru.sulgik.periods.domain.room.PeriodDao


@Module
class MainDatabaseModule {

    @Single
    fun mainDatabase(
        applicationContext: Context,
    ): MainDnevnikXDatabase {
        return Room.databaseBuilder(applicationContext, MainDnevnikXDatabase::class.java, "db")
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

}