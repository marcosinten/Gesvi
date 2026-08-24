package com.gestionviajes.feature.trips.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.feature.trips.presentation.TripViewModel

/**
 * Pantalla principal de viajes. (Stub)
 */
@Composable
fun TripListScreen(
    onNavigateToSeatMap: (Long) -> Unit,
    viewModel: TripViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // TODO: Implementar UI real
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de Viajes (Cargando: ${uiState.isLoading}, Viajes: ${uiState.trips.size})")
    }
}
