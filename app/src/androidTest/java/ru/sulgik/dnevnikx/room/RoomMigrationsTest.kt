package ru.sulgik.dnevnikx.room

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RoomMigrationsTest {

    private val TEST_DB = "test_db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        DnevnikXDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrationTestFrom1To2() {
        helper.createDatabase(TEST_DB, 1).apply {
            // Prepare for the next version.
            close()
        }
        helper.runMigrationsAndValidate(TEST_DB, 2, true)
    }

}