package ru.sulgik.room.main

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import ru.sulgik.marks.domain.migrations.MARKS_MIGRATION_1_2

class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MainDnevnikXDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2() {
//        var db = helper.createDatabase(TEST_DB, 1).apply {
//            execSQL(sql = "INSERT INTO AccountEntity(`id`) VALUES(1)")
//            execSQL(sql = "INSERT INTO MarksPeriodEntity(`id`, `accountId`, `start`, `end`) VALUES(1, 1, 10000,100000)")
//            execSQL(sql = "INSERT INTO MarksLessonEntity(`id`, `title`, `average`, `averageValue`, `periodId`) VALUES(1, `Англ яз`, `5`, 5, 1)")
//            execSQL(sql = "INSERT INTO MarksLessonMarkEntity(`id`, `mark`, `value`, `date`) VALUES(1,`5`,5,100)")
//            close()
//        }

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MARKS_MIGRATION_1_2)
    }
}