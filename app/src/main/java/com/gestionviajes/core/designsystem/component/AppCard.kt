package com.gestionviajes.core.designsystem.component

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Tarjeta base para listar elementos (viajes, pasajeros).
 * TODO: Implementar elevación y bordes corporativos.
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(modifier = modifier) {
        content()
    }
}
