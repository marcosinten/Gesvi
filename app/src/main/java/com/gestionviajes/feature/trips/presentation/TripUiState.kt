package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.Trip

/**
 * Estado que representa TODO lo visible en la pantalla de lista de viajes.
 *
 * El View (Compose) solo lee esto para renderizarse.
 */
data class TripUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val errorMessage: String? = null
)
