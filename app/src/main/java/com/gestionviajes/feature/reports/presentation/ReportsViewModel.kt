package com.gestionviajes.feature.reports.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.gestionviajes.core.common.Result
import com.gestionviajes.core.common.Constants.FLOW_TIMEOUT_MS
import com.gestionviajes.core.domain.NoParams
import com.gestionviajes.feature.trips.domain.model.TourDetail
import com.gestionviajes.feature.trips.domain.model.Trip
import com.gestionviajes.feature.trips.domain.usecase.GetTourDetailUseCase
import com.gestionviajes.feature.trips.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ReportsUiState(
    val isLoading: Boolean = false,
    val trips: List<Trip> = emptyList(),
    val selectedTourId: Long? = null,
    val detail: TourDetail? = null,
    val loadFailed: Boolean = false,
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel @Inject constructor(
    getTripsUseCase: GetTripsUseCase,
    private val getTourDetailUseCase: GetTourDetailUseCase,
) : ViewModel() {

    private val selectedTourId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<ReportsUiState> = getTripsUseCase(NoParams)
        .flatMapLatest { tripsResult ->
            when (tripsResult) {
                Result.Loading -> flowOf(ReportsUiState(isLoading = true))
                is Result.Error -> flowOf(ReportsUiState(loadFailed = true))
                is Result.Success -> selectedTourId.flatMapLatest { requestedTourId ->
                    val trips = tripsResult.data
                    val tourId = requestedTourId?.takeIf { requested ->
                        trips.any { trip -> trip.id == requested }
                    } ?: trips.firstOrNull()?.id
                    if (tourId == null) {
                        flowOf(ReportsUiState(trips = trips))
                    } else {
                        getTourDetailUseCase(tourId).map { detailResult ->
                            when (detailResult) {
                                Result.Loading -> ReportsUiState(
                                    isLoading = true,
                                    trips = trips,
                                    selectedTourId = tourId,
                                )
                                is Result.Error -> ReportsUiState(
                                    trips = trips,
                                    selectedTourId = tourId,
                                    loadFailed = true,
                                )
                                is Result.Success -> ReportsUiState(
                                    trips = trips,
                                    selectedTourId = tourId,
                                    detail = detailResult.data,
                                )
                            }
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = ReportsUiState(isLoading = true),
        )

    fun selectTour(tourId: Long) {
        selectedTourId.value = tourId
    }

    /**
     * Detalles de todos los tours registrados, para el informe limpio de
     * viaje. Deriva del mismo estado de dominio que el resto de proyecciones
     * y no crea una fuente de datos independiente.
     */
    val allTourDetails: StateFlow<List<TourDetail>> = getTripsUseCase(NoParams)
        .flatMapLatest { tripsResult ->
            when (tripsResult) {
                Result.Loading -> flowOf(emptyList())
                is Result.Error -> flowOf(emptyList())
                is Result.Success -> {
                    val trips = tripsResult.data
                    if (trips.isEmpty()) {
                        flowOf(emptyList())
                    } else {
                        combine(trips.map { trip -> getTourDetailUseCase(trip.id) }) { details ->
                            details.mapNotNull { result ->
                                if (result is Result.Success) result.data else null
                            }
                        }
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = emptyList(),
        )
}
