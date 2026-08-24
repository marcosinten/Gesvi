package com.gestionviajes.feature.trips.data.di

import com.gestionviajes.feature.trips.data.repository.TripRepositoryImpl
import com.gestionviajes.feature.trips.domain.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TripDataModule {

    @Binds
    abstract fun bindTripRepository(implementation: TripRepositoryImpl): TripRepository
}
