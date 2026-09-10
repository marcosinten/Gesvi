package com.gestionviajes.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object AppDatabaseMigrations {
    /** Conserva los Tours existentes sin inventar destino ni supervisor. */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `trips` ADD COLUMN `destination` TEXT")
            database.execSQL("ALTER TABLE `trips` ADD COLUMN `supervisor` TEXT")
        }
    }

    /** Separa la disponibilidad de una tarifa de su importe almacenado. */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Room espera que roundTripFareCents sea nullable en v4. Se respaldan
            // primero las tablas hijas para reconstruir trips sin activar cascadas.
            database.execSQL(
                "CREATE TEMP TABLE `_bookings_backup` AS SELECT * FROM `bookings`",
            )
            database.execSQL(
                "CREATE TEMP TABLE `_booking_seats_backup` AS " +
                    "SELECT * FROM `booking_seat_cross_ref`",
            )
            database.execSQL(
                "CREATE TEMP TABLE `_payments_backup` AS SELECT * FROM `payments`",
            )
            database.execSQL("DROP TABLE `payments`")
            database.execSQL("DROP TABLE `booking_seat_cross_ref`")
            database.execSQL("DROP TABLE `bookings`")

            database.execSQL(
                """
                CREATE TABLE `trips_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `destination` TEXT,
                    `supervisor` TEXT,
                    `dateMillis` INTEGER NOT NULL,
                    `seatCount` INTEGER NOT NULL,
                    `freightCents` INTEGER NOT NULL,
                    `roundTripFareCents` INTEGER,
                    `outboundFareCents` INTEGER,
                    `returnFareCents` INTEGER,
                    `isRoundTripFareEnabled` INTEGER NOT NULL,
                    `isOutboundFareEnabled` INTEGER NOT NULL,
                    `isReturnFareEnabled` INTEGER NOT NULL
                )
                """.trimIndent(),
            )
            database.execSQL(
                """
                INSERT INTO `trips_new` (
                    `id`,
                    `destination`,
                    `supervisor`,
                    `dateMillis`,
                    `seatCount`,
                    `freightCents`,
                    `roundTripFareCents`,
                    `outboundFareCents`,
                    `returnFareCents`,
                    `isRoundTripFareEnabled`,
                    `isOutboundFareEnabled`,
                    `isReturnFareEnabled`
                )
                SELECT
                    `id`,
                    `destination`,
                    `supervisor`,
                    `dateMillis`,
                    `seatCount`,
                    `freightCents`,
                    `roundTripFareCents`,
                    `outboundFareCents`,
                    `returnFareCents`,
                    1,
                    CASE WHEN `outboundFareCents` IS NULL THEN 0 ELSE 1 END,
                    CASE WHEN `returnFareCents` IS NULL THEN 0 ELSE 1 END
                FROM `trips`
                """.trimIndent(),
            )
            database.execSQL("DROP TABLE `trips`")
            database.execSQL("ALTER TABLE `trips_new` RENAME TO `trips`")

            database.execSQL(
                """
                CREATE TABLE `bookings` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `tripId` INTEGER NOT NULL,
                    `passengerName` TEXT NOT NULL,
                    `totalFare` REAL NOT NULL,
                    FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            database.execSQL(
                "CREATE INDEX `index_bookings_tripId` ON `bookings` (`tripId`)",
            )
            database.execSQL(
                """
                CREATE TABLE `booking_seat_cross_ref` (
                    `bookingId` INTEGER NOT NULL,
                    `seatNumber` TEXT NOT NULL,
                    PRIMARY KEY(`bookingId`, `seatNumber`)
                )
                """.trimIndent(),
            )
            database.execSQL(
                """
                CREATE TABLE `payments` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `bookingId` INTEGER NOT NULL,
                    `amount` REAL NOT NULL,
                    `dateMillis` INTEGER NOT NULL,
                    FOREIGN KEY(`bookingId`) REFERENCES `bookings`(`id`)
                        ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent(),
            )
            database.execSQL(
                "CREATE INDEX `index_payments_bookingId` ON `payments` (`bookingId`)",
            )

            database.execSQL(
                "INSERT INTO `bookings` SELECT * FROM `_bookings_backup`",
            )
            database.execSQL(
                "INSERT INTO `booking_seat_cross_ref` SELECT * FROM `_booking_seats_backup`",
            )
            database.execSQL(
                "INSERT INTO `payments` SELECT * FROM `_payments_backup`",
            )
            database.execSQL("DROP TABLE `_bookings_backup`")
            database.execSQL("DROP TABLE `_booking_seats_backup`")
            database.execSQL("DROP TABLE `_payments_backup`")
        }
    }

    /** Normaliza ventas a centavos y conserva tipo de pasaje por asiento. */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TEMP TABLE `_booking_seats_v4_backup` AS SELECT * FROM `booking_seat_cross_ref`")
            database.execSQL("CREATE TEMP TABLE `_payments_v4_backup` AS SELECT * FROM `payments`")
            database.execSQL("CREATE TABLE `bookings_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tripId` INTEGER NOT NULL, `passengerName` TEXT NOT NULL, FOREIGN KEY(`tripId`) REFERENCES `trips`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
            database.execSQL("INSERT INTO `bookings_new` (`id`,`tripId`,`passengerName`) SELECT `id`,`tripId`,`passengerName` FROM `bookings`")
            database.execSQL("DROP TABLE `booking_seat_cross_ref`")
            database.execSQL("DROP TABLE `payments`")
            database.execSQL("DROP TABLE `bookings`")
            database.execSQL("ALTER TABLE `bookings_new` RENAME TO `bookings`")
            database.execSQL("CREATE INDEX `index_bookings_tripId` ON `bookings` (`tripId`)")
            database.execSQL("CREATE TABLE `booking_seat_cross_ref` (`bookingId` INTEGER NOT NULL, `seatNumber` INTEGER NOT NULL, `fareType` TEXT NOT NULL, PRIMARY KEY(`bookingId`, `seatNumber`))")
            database.execSQL("CREATE TABLE `payments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `bookingId` INTEGER NOT NULL, `amountCents` INTEGER NOT NULL, `dateMillis` INTEGER NOT NULL, FOREIGN KEY(`bookingId`) REFERENCES `bookings`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
            database.execSQL("CREATE INDEX `index_payments_bookingId` ON `payments` (`bookingId`)")
            database.execSQL("INSERT INTO `booking_seat_cross_ref` (`bookingId`,`seatNumber`,`fareType`) SELECT `bookingId`, CAST(`seatNumber` AS INTEGER), 'ROUND_TRIP' FROM `_booking_seats_v4_backup`")
            database.execSQL("INSERT INTO `payments` (`id`,`bookingId`,`amountCents`,`dateMillis`) SELECT `id`,`bookingId`, CAST(ROUND(`amount` * 100) AS INTEGER), `dateMillis` FROM `_payments_v4_backup`")
            database.execSQL("DROP TABLE `_booking_seats_v4_backup`")
            database.execSQL("DROP TABLE `_payments_v4_backup`")
        }
    }

    /**
     * Agrega hora de salida y hora de venida al Tour (tramos IDA/VENIDA).
     * No toca `booking_seat_cross_ref`: la ocupación por tramo se deriva del
     * `fareType` ya existente, no requiere una columna nueva. Ver
     * docs/DATA_MODEL.md.
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `trips` ADD COLUMN `horaSalidaMillis` INTEGER")
            database.execSQL("ALTER TABLE `trips` ADD COLUMN `horaVenidaMillis` INTEGER")
        }
    }
}
