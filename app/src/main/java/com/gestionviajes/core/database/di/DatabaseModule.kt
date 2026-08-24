package com.gestionviajes.core.database.di

import android.content.Context
import androidx.room.Room
import com.gestionviajes.core.common.Constants
import com.gestionviajes.core.database.AppDatabase
import com.gestionviajes.feature.bookings.data.local.BookingDao
import com.gestionviajes.feature.collections.data.local.PaymentDao
import com.gestionviajes.feature.trips.data.local.TripDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt que provee la instancia única de [AppDatabase] y los DAOs.
 *
 * Instalado en [SingletonComponent] para que la BD sea un singleton.
 * Los DAOs NO son singletons — Room devuelve la misma instancia internamente.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME,
        )
        // TODO: Antes de producción, reemplazar con migraciones explícitas.
        // Ver docs/DATA_MODEL.md sección "Migraciones".
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()

    @Provides
    fun provideBookingDao(db: AppDatabase): BookingDao = db.bookingDao()

    @Provides
    fun providePaymentDao(db: AppDatabase): PaymentDao = db.paymentDao()
}
