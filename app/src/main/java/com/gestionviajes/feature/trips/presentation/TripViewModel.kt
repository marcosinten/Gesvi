package com.gestionviajes.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionviajes.core.common.Constants.FLOW_TIMEOUT_MS
import com.gestionviajes.core.common.Result
import com.gestionviajes.core.domain.NoParams
import com.gestionviajes.feature.trips.domain.usecase.GetTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    getTripsUseCase: GetTripsUseCase,
) : ViewModel() {

    val uiState: StateFlow<TripUiState> = getTripsUseCase(NoParams)
        .map { tripsResult ->
            when (tripsResult) {
                is Result.Loading -> TripUiState(isLoading = true)
                is Result.Error -> TripUiState(errorMessage = tripsResult.message)
                is Result.Success -> TripUiState(trips = tripsResult.data)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
            initialValue = TripUiState(isLoading = true),
        )
}
