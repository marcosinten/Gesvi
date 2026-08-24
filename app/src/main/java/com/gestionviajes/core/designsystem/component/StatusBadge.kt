package com.gestionviajes.core.designsystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Etiqueta visual pequeña para estados (ej. "En curso", "Finalizado").
 * TODO: Implementar fondo redondeado y colores semánticos.
 */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(text = text, modifier = modifier)
}
