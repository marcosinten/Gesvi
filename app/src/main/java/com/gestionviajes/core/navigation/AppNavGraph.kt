package com.gestionviajes.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gestionviajes.feature.accounts.presentation.AccountsScreen
import com.gestionviajes.feature.history.presentation.HistoryScreen
import com.gestionviajes.feature.passenger_list.presentation.PassengerListScreen
import com.gestionviajes.feature.reports.presentation.RefundsScreen
import com.gestionviajes.feature.reports.presentation.ReportsScreen
import com.gestionviajes.feature.reports.presentation.TourReportScreen
import com.gestionviajes.feature.reports.presentation.TripReportScreen
import com.gestionviajes.feature.seats.presentation.SeatMapScreen
import com.gestionviajes.feature.settings.presentation.SettingsScreen
import com.gestionviajes.feature.trips.presentation.screen.CreateTourScreen
import com.gestionviajes.feature.trips.presentation.screen.EditTourScreen
import com.gestionviajes.feature.trips.presentation.screen.TripListScreen
import com.gestionviajes.feature.trips.presentation.screen.TourDetailScreen

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
                onCreateTour = {
                    navController.navigate(AppDestination.CreateTour)
                },
                onOpenTour = { tourId ->
                    navController.navigate(AppDestination.TourDetail(tourId))
                },
                onEditTour = { tourId ->
                    navController.navigate(AppDestination.EditTour(tourId))
                },
                onOpenReports = {
                    navController.navigateToTopLevelDestination(AppDestination.Reports)
                },
                onOpenHistory = {
                    navController.navigateToTopLevelDestination(AppDestination.History)
                },
            )
        }

        composable<AppDestination.Reports> {
            ReportsScreen(
                onNavigateToHome = {
                    navController.navigateToTopLevelDestination(AppDestination.TripList)
                },
                onNavigateToHistory = {
                    navController.navigateToTopLevelDestination(AppDestination.History)
                },
                onOpenTripReport = {
                    navController.navigate(AppDestination.TripReport)
                },
                onOpenTourReport = {
                    navController.navigate(AppDestination.TourReport)
                },
                onOpenRefunds = {
                    navController.navigate(AppDestination.Refunds)
                },
            )
        }

        composable<AppDestination.TripReport> {
            TripReportScreen(
                onNavigateToHome = {
                    navController.navigateToTopLevelDestination(AppDestination.TripList)
                },
                onNavigateToHistory = {
                    navController.navigateToTopLevelDestination(AppDestination.History)
                },
            )
        }

        composable<AppDestination.TourReport> {
            TourReportScreen(
                onNavigateToHome = {
                    navController.navigateToTopLevelDestination(AppDestination.TripList)
                },
                onNavigateToHistory = {
                    navController.navigateToTopLevelDestination(AppDestination.History)
                },
            )
        }

        composable<AppDestination.Refunds> {
            RefundsScreen(
                onNavigateToHome = {
                    navController.navigateToTopLevelDestination(AppDestination.TripList)
                },
                onNavigateToHistory = {
                    navController.navigateToTopLevelDestination(AppDestination.History)
                },
            )
        }

        composable<AppDestination.History> {
            HistoryScreen(
                onNavigateToReports = {
                    navController.navigateToTopLevelDestination(AppDestination.Reports)
                },
                onNavigateToHome = {
                    navController.navigateToTopLevelDestination(AppDestination.TripList)
                },
                onOpenTour = { tourId ->
                    navController.navigate(AppDestination.TourDetail(tourId))
                },
            )
        }

        composable<AppDestination.CreateTour> {
            CreateTourScreen(
                onNavigateBack = { navController.popBackStack() },
                onTourCreated = { tourId ->
                    navController.popBackStack()
                    navController.navigate(AppDestination.TourDetail(tourId))
                },
            )
        }

        composable<AppDestination.EditTour> {
            EditTourScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable<AppDestination.TourDetail> {
            TourDetailScreen(onNavigateBack = { navController.popBackStack() })
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
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
