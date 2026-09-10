package com.gestionviajes.feature.trips.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionviajes.feature.trips.domain.usecase.CreateTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateTourViewModel @Inject constructor(
    private val createTripUseCase: CreateTripUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTourUiState.initial())
    val uiState: StateFlow<CreateTourUiState> = _uiState.asStateFlow()

    private val _effects = Channel<CreateTourEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: CreateTourEvent) {
        when (event) {
            is CreateTourEvent.DestinationChanged -> updateField(CreateTourField.Destination) {
                copy(destination = event.value)
            }
            is CreateTourEvent.SupervisorChanged -> updateField(CreateTourField.Supervisor) {
                copy(supervisor = event.value)
            }
            is CreateTourEvent.DayChanged -> updateField(CreateTourField.Date) {
                copy(day = event.value.filter(Char::isDigit).take(2))
            }
            is CreateTourEvent.MonthChanged -> updateField(CreateTourField.Date) {
                copy(month = event.value)
            }
            is CreateTourEvent.YearChanged -> updateField(CreateTourField.Date) {
                copy(year = event.value)
            }
            is CreateTourEvent.TimeChanged -> updateField(CreateTourField.Time) {
                copy(hour = event.hour, minute = event.minute)
            }
            is CreateTourEvent.VenidaTimeChanged -> updateField(CreateTourField.ReturnTime) {
                copy(venidaHour = event.hour, venidaMinute = event.minute)
            }
            is CreateTourEvent.VenidaDefinedChanged -> updateField(CreateTourField.ReturnTime) {
                copy(isVenidaDefined = event.defined)
            }
            is CreateTourEvent.SeatCountChanged -> updateField(CreateTourField.SeatCount) {
                copy(seatCount = event.value.filter(Char::isDigit))
            }
            is CreateTourEvent.FreightChanged -> updateField(CreateTourField.Freight) {
                copy(freight = event.value)
            }
            CreateTourEvent.EnableRoundTripFare -> updateField(CreateTourField.FareConfiguration) {
                copy(isRoundTripFareEnabled = true)
            }
            CreateTourEvent.RemoveRoundTripFare -> updateField(CreateTourField.FareConfiguration) {
                if (enabledFareCount == 1) this else copy(isRoundTripFareEnabled = false)
            }
            is CreateTourEvent.RoundTripFareChanged -> updateField(CreateTourField.RoundTripFare) {
                copy(roundTripFare = event.value)
            }
            CreateTourEvent.EnableOutboundFare -> updateField(CreateTourField.FareConfiguration) {
                copy(isOutboundFareEnabled = true)
            }
            CreateTourEvent.RemoveOutboundFare -> updateField(CreateTourField.FareConfiguration) {
                if (enabledFareCount == 1) this else copy(isOutboundFareEnabled = false)
            }
            is CreateTourEvent.OutboundFareChanged -> updateField(CreateTourField.OutboundFare) {
                copy(outboundFare = event.value)
            }
            CreateTourEvent.EnableReturnFare -> updateField(CreateTourField.FareConfiguration) {
                copy(isReturnFareEnabled = true)
            }
            CreateTourEvent.RemoveReturnFare -> updateField(CreateTourField.FareConfiguration) {
                if (enabledFareCount == 1) this else copy(isReturnFareEnabled = false)
            }
            is CreateTourEvent.ReturnFareChanged -> updateField(CreateTourField.ReturnFare) {
                copy(returnFare = event.value)
            }
            CreateTourEvent.Submit -> submit()
        }
    }

    private fun updateField(
        field: CreateTourField,
        transform: CreateTourUiState.() -> CreateTourUiState,
    ) {
        _uiState.update { state ->
            state.transform().copy(
                invalidFields = state.invalidFields - field,
                saveFailed = false,
            )
        }
    }

    private fun submit() {
        val currentState = _uiState.value
        if (currentState.isSaving) return

        val validation = validateCreateTourForm(currentState)
        val trip = validation.trip
        if (trip == null) {
            _uiState.update {
                it.copy(
                    invalidFields = validation.invalidFields,
                    saveFailed = false,
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, saveFailed = false) }
        viewModelScope.launch {
            try {
                val tourId = createTripUseCase(trip)
                _uiState.update { state -> state.copy(isSaving = false) }
                _effects.send(CreateTourEffect.Created(tourId))
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _uiState.update { state ->
                    state.copy(isSaving = false, saveFailed = true)
                }
            }
        }
    }
}
