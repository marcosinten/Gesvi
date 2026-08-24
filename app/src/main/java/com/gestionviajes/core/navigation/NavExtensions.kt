package com.gestionviajes.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Extensiones para simplificar la navegación común.
 */

fun NavController.navigateToTopLevelDestination(destination: AppDestination) {
    navigate(destination) {
        // Evitar acumular múltiples copias del destino inicial
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        // Evitar múltiples copias de la misma pantalla en el top
        launchSingleTop = true
        // Restaurar estado al re-seleccionar un destino
        restoreState = true
    }
}
