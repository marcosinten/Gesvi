package com.gestionviajes.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionviajes.core.common.Constants.FLOW_TIMEOUT_MS
import com.gestionviajes.core.common.Result
import com.gestionviajes.core.domain.NoParams
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.usecase.CreateTripUseCase
import com.gestionviajes.feature.trips.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    getTripsUseCase: GetTripsUseCase,
    private val createTripUseCase: CreateTripUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    /**
     * El flujo de viajes proviene directamente del dominio y la BD.
     * Combinamos esto con nuestros propios flujos locales (loading, error)
     * para producir el UiState final.
     */
    val uiState: StateFlow<TripUiState> = combine(
        getTripsUseCase(NoParams),
        _isLoading,
        _errorMessage
    ) { tripsResult, loading, error ->
        when (tripsResult) {
            is Result.Loading -> TripUiState(isLoading = true)
            is Result.Error -> TripUiState(isLoading = loading, errorMessage = tripsResult.message)
            is Result.Success -> TripUiState(isLoading = loading, errorMessage = error, trips = tripsResult.data)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
        initialValue = TripUiState(isLoading = true)
    )

    fun onEvent(event: TripEvent) {
        when (event) {
            is TripEvent.CreateTrip -> createTrip(
                origin = event.origin,
                destination = event.destination,
                dateMillis = event.dateMillis,
                defaultFare = event.defaultFare
            )
            is TripEvent.SelectTrip -> {
                // Manejado en la UI vía callback a Navigation
            }
            TripEvent.ClearError -> _errorMessage.value = null
        }
    }

    private fun createTrip(origin: String, destination: String, dateMillis: Long, defaultFare: Double) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newTrip = Trip(
                    origin = origin,
                    destination = destination,
                    dateMillis = dateMillis,
                    defaultFare = defaultFare
                )
                createTripUseCase(newTrip)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Error al crear viaje"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
