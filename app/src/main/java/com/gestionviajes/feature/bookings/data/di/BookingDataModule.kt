package com.gestionviajes.feature.bookings.data.di

import com.gestionviajes.feature.bookings.data.repository.BookingRepositoryImpl
import com.gestionviajes.feature.bookings.domain.repository.BookingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BookingDataModule {
    @Binds abstract fun bindBookingRepository(implementation: BookingRepositoryImpl): BookingRepository
}
