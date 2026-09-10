package com.gestionviajes.feature.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gestionviajes.feature.trips.domain.usecase.DeleteTripUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryDeleteUiState(
    val tourIdPendingDeletion: Long? = null,
    val isDeleting: Boolean = false,
    val deleteFailed: Boolean = false,
)

sealed interface HistoryDeleteEvent {
    data class DeleteRequested(val tourId: Long) : HistoryDeleteEvent
    data object ConfirmDelete : HistoryDeleteEvent
    data object DismissDelete : HistoryDeleteEvent
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val deleteTripUseCase: DeleteTripUseCase,
) : ViewModel() {

    private val _deleteUiState = MutableStateFlow(HistoryDeleteUiState())
    val deleteUiState: StateFlow<HistoryDeleteUiState> = _deleteUiState.asStateFlow()

    fun onEvent(event: HistoryDeleteEvent) {
        when (event) {
            is HistoryDeleteEvent.DeleteRequested -> _deleteUiState.value = HistoryDeleteUiState(
                tourIdPendingDeletion = event.tourId,
            )
            HistoryDeleteEvent.DismissDelete -> {
                if (!_deleteUiState.value.isDeleting) _deleteUiState.value = HistoryDeleteUiState()
            }
            HistoryDeleteEvent.ConfirmDelete -> deleteTour()
        }
    }

    private fun deleteTour() {
        val tourId = _deleteUiState.value.tourIdPendingDeletion ?: return
        if (_deleteUiState.value.isDeleting) return
        _deleteUiState.update { it.copy(isDeleting = true, deleteFailed = false) }
        viewModelScope.launch {
            try {
                deleteTripUseCase(tourId)
                _deleteUiState.value = HistoryDeleteUiState()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _deleteUiState.update { it.copy(isDeleting = false, deleteFailed = true) }
            }
        }
    }
}
