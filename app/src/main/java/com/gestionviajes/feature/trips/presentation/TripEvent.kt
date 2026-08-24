package com.gestionviajes.feature.trips.presentation

/**
 * Eventos que ocurren en la UI y deben ser procesados por el ViewModel.
 */
sealed interface TripEvent {
    data class CreateTrip(
        val origin: String,
        val destination: String,
        val dateMillis: Long,
        val defaultFare: Double
    ) : TripEvent
    data class SelectTrip(val tripId: Long) : TripEvent
    data object ClearError : TripEvent
}
