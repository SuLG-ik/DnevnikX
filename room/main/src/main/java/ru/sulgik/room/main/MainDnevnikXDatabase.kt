package ru.sulgik.room.main

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.sulgik.account.domain.AccountDao
import ru.sulgik.account.domain.AccountDataDao
import ru.sulgik.account.domain.AccountDataEntity
import ru.sulgik.account.domain.AccountEntity
import ru.sulgik.diary.domain.DiaryDao
import ru.sulgik.diary.domain.DiaryDateEntity
import ru.sulgik.diary.domain.DiaryDateLessonEntity
import ru.sulgik.diary.domain.LessonFileEntity
import ru.sulgik.diary.domain.LessonHomeworkEntity
import ru.sulgik.diary.domain.LessonMarkEntity
import ru.sulgik.finalmarks.domain.FinalMarksDao
import ru.sulgik.finalmarks.domain.FinalMarksLessonEntity
import ru.sulgik.finalmarks.domain.FinalMarksLessonMarkEntity
import ru.sulgik.marks.domain.MarksDao
import ru.sulgik.marks.domain.MarksLessonEntity
import ru.sulgik.marks.domain.MarksLessonMarkEntity
import ru.sulgik.marks.domain.MarksPeriodEntity
import ru.sulgik.periods.domain.room.HostPeriodEntity
import ru.sulgik.periods.domain.room.NestedPeriodEntity
import ru.sulgik.periods.domain.room.PeriodDao

@Database(
    entities = [
        AccountEntity::class, AccountDataEntity::class,
        HostPeriodEntity::class, NestedPeriodEntity::class,
        DiaryDateEntity::class, DiaryDateLessonEntity::class, LessonFileEntity::class, LessonHomeworkEntity::class, LessonMarkEntity::class,
        MarksPeriodEntity::class, MarksLessonEntity::class, MarksLessonMarkEntity::class,
        FinalMarksLessonEntity::class, FinalMarksLessonMarkEntity::class,
    ],
    version = 3,
    autoMigrations = [AutoMigration(1, 2), AutoMigration(2, 3)],
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class MainDnevnikXDatabase : RoomDatabase() {

    abstract val accountDao: AccountDao
    abstract val accountDataDao: AccountDataDao
    abstract val periodDao: PeriodDao
    abstract val diaryDao: DiaryDao
    abstract val marksDao: MarksDao
    abstract val finalMarksDao: FinalMarksDao

}