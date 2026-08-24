package com.gestionviajes.feature.trips.presentation

import com.gestionviajes.feature.trips.domain.model.Trip

/** Estado inmutable de la pantalla inicial de Tours. */
data class TripUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val errorMessage: String? = null
)
