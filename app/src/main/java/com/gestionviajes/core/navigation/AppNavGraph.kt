package com.gestionviajes.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gestionviajes.feature.accounts.presentation.AccountsScreen
import com.gestionviajes.feature.passenger_list.presentation.PassengerListScreen
import com.gestionviajes.feature.seats.presentation.SeatMapScreen
import com.gestionviajes.feature.settings.presentation.SettingsScreen
import com.gestionviajes.feature.trips.presentation.screen.TripListScreen

/**
 * Grafo de navegación principal.
 *
 * Utiliza las rutas tipadas de [AppDestination].
 * Aquí se configuran todas las transiciones entre pantallas.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.TripList
    ) {
        composable<AppDestination.TripList> {
            TripListScreen(
                onNavigateToSeatMap = { tripId ->
                    navController.navigate(AppDestination.SeatMap(tripId))
                }
            )
        }

        composable<AppDestination.SeatMap> { backStackEntry ->
            // El viewmodel puede extraer los argumentos automáticamente usando SavedStateHandle.toRoute()
            SeatMapScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPassengerList = { tripId ->
                    navController.navigate(AppDestination.PassengerList(tripId))
                }
            )
        }

        composable<AppDestination.PassengerList> {
            PassengerListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<AppDestination.Accounts> {
            AccountsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<AppDestination.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
