package com.gestionviajes.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gestionviajes.core.common.Constants
import com.gestionviajes.feature.bookings.data.local.BookingDao
import com.gestionviajes.feature.bookings.data.local.BookingEntity
import com.gestionviajes.feature.bookings.data.local.BookingSeatCrossRef
import com.gestionviajes.feature.collections.data.local.PaymentDao
import com.gestionviajes.feature.collections.data.local.PaymentEntity
import com.gestionviajes.feature.trips.data.local.TripDao
import com.gestionviajes.feature.trips.data.local.TripEntity

/**
 * Base de datos Room centralizada de la aplicación.
 *
 * Todas las entidades se registran aquí aunque sus DAOs vivan en paquetes
 * de feature. Esto es intencional:
 * - Un único archivo SQLite = un único límite transaccional ACID.
 * - Los DAOs se inyectan de forma independiente a través de [DatabaseModule].
 *
 * Al extraer features a módulos Gradle independientes, solo este archivo
 * y [DatabaseModule] necesitan actualizarse.
 *
 * ⚠️ Incrementar [version] y proveer una [Migration] ante cualquier
 * cambio de esquema. Ver docs/DATA_MODEL.md.
 */
@Database(
    entities = [
        TripEntity::class,
        BookingEntity::class,
        BookingSeatCrossRef::class,
        PaymentEntity::class,
    ],
    version = Constants.DATABASE_VERSION,
    exportSchema = true,           // genera JSON en app/schemas/ para control de migraciones
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun bookingDao(): BookingDao
    abstract fun paymentDao(): PaymentDao
}
