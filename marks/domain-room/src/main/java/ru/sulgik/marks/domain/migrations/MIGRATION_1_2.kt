package ru.sulgik.marks.domain.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MARKS_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE MarksLessonMarkEntity")
        database.execSQL("DROP TABLE MarksLessonEntity")
        database.execSQL("CREATE TABLE `MarksLessonEntity` (`title` TEXT NOT NULL, `average` TEXT NOT NULL, `averageValue` INTEGER NOT NULL, `periodId` INTEGER NOT NULL, `uid` INTEGER PRIMARY KEY NOT NULL, FOREIGN KEY(`periodId`) REFERENCES `MarksPeriodEntity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        database.execSQL("CREATE TABLE `MarksLessonMarkEntity` (`mark` TEXT NOT NULL, `value` INTEGER NOT NULL, `date` INTEGER NOT NULL, `message` TEXT, `lessonUid` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`lessonUid`) REFERENCES `MarksLessonEntity`(`uid`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}
