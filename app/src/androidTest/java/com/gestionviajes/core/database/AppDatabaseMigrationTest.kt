package com.gestionviajes.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    @get:Rule
    val migrationHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
    )

    @After
    fun deleteDatabase() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun migrate2To3PreservesTourAndLeavesNewFieldsUnknown() {
        migrationHelper.createDatabase(TEST_DATABASE, 2).apply {
            execSQL(
                """
                INSERT INTO trips (
                    dateMillis,
                    seatCount,
                    freightCents,
                    roundTripFareCents,
                    outboundFareCents,
                    returnFareCents
                ) VALUES (1800000000000, 40, 50000, 7000, NULL, NULL)
                """.trimIndent(),
            )
            close()
        }

        val database = migrationHelper.runMigrationsAndValidate(
            TEST_DATABASE,
            3,
            true,
            AppDatabaseMigrations.MIGRATION_2_3,
        )

        try {
            database.query("SELECT * FROM trips").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(40, cursor.getInt(cursor.getColumnIndexOrThrow("seatCount")))
                assertEquals(50_000L, cursor.getLong(cursor.getColumnIndexOrThrow("freightCents")))
                assertTrue(cursor.isNull(cursor.getColumnIndexOrThrow("destination")))
                assertTrue(cursor.isNull(cursor.getColumnIndexOrThrow("supervisor")))
            }
        } finally {
            database.close()
        }
    }

    private companion object {
        const val TEST_DATABASE = "app-database-migration-test"
    }
}
