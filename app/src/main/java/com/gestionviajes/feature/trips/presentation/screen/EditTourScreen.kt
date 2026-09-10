package com.gestionviajes.feature.trips.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppEmptyState
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.presentation.CreateTourEvent
import com.gestionviajes.feature.trips.presentation.CreateTourField
import com.gestionviajes.feature.trips.presentation.EditTourFaresEffect
import com.gestionviajes.feature.trips.presentation.EditTourFaresViewModel

@Composable
fun EditTourScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditTourFaresViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loaded by viewModel.loaded.collectAsStateWithLifecycle()
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect == EditTourFaresEffect.Saved) currentOnNavigateBack()
        }
    }

    when (loaded) {
        null -> {
            // Aún cargando
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        false -> {
            // Tour no encontrado
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppEmptyState(
                    title = stringResource(R.string.tour_detail_not_found_title),
                    message = stringResource(R.string.tour_detail_not_found_message),
                    icon = Icons.Rounded.Warning,
                )
            }
            return
        }
        else -> Unit
    }

    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(),
        title = stringResource(R.string.edit_tour_title),
        subtitle = stringResource(R.string.edit_tour_fares_subtitle),
        headerIcon = Icons.Rounded.Edit,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
        bottomBar = {
            CreateTourSaveBar(
                isSaving = uiState.isSaving,
                hasValidationErrors = uiState.invalidFields.isNotEmpty(),
                saveFailed = uiState.saveFailed,
                onSubmit = { viewModel.onEvent(CreateTourEvent.Submit) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentPadding = PaddingValues(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            item {
                FormSection(
                    title = stringResource(R.string.tour_information_section),
                    icon = Icons.Rounded.Person,
                    iconContainerColor = colors.accentSkyContainer,
                    iconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                    FormTextField(
                        value = uiState.destination,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.DestinationChanged(it)) },
                        label = stringResource(R.string.destination_label),
                        errorMessage = if (CreateTourField.Destination in uiState.invalidFields) stringResource(R.string.destination_required_error) else null,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    )
                    FormTextField(
                        value = uiState.supervisor,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.SupervisorChanged(it)) },
                        label = stringResource(R.string.supervisor_label),
                        errorMessage = if (CreateTourField.Supervisor in uiState.invalidFields) stringResource(R.string.supervisor_required_error) else null,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                    )
                }
            }

            item {
                DateAndTimeSection(uiState = uiState, onEvent = viewModel::onEvent)
            }

            item {
                FormSection(
                    title = stringResource(R.string.tour_capacity_section),
                    icon = Icons.Rounded.EventSeat,
                    iconContainerColor = colors.accentGreenContainer,
                    iconContentColor = colors.onSuccessContainer,
                ) {
                    FormTextField(
                        value = uiState.seatCount,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.SeatCountChanged(it)) },
                        label = stringResource(R.string.seat_count_label),
                        errorMessage = if (CreateTourField.SeatCount in uiState.invalidFields) stringResource(R.string.seat_count_error) else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    )
                    MoneyTextField(
                        value = uiState.freight,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.FreightChanged(it)) },
                        label = stringResource(R.string.freight_label),
                        errorMessage = if (CreateTourField.Freight in uiState.invalidFields) stringResource(R.string.freight_error) else null,
                    )
                }
            }

            item {
                FormSection(
                    title = stringResource(R.string.tour_fares_section),
                    description = stringResource(R.string.tour_fares_description),
                    icon = Icons.Rounded.Payments,
                    iconContainerColor = colors.accentPinkContainer,
                    iconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    if (CreateTourField.FareConfiguration in uiState.invalidFields) {
                        FieldError(stringResource(R.string.minimum_one_fare_error))
                    }

                    FareConfigurationInput(
                        title = stringResource(R.string.round_trip_fare_label),
                        value = uiState.roundTripFare,
                        enabled = uiState.isRoundTripFareEnabled,
                        canRemove = uiState.enabledFareCount > 1,
                        errorMessage = if (CreateTourField.RoundTripFare in uiState.invalidFields) stringResource(R.string.fare_error) else null,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.RoundTripFareChanged(it)) },
                        onEnable = { viewModel.onEvent(CreateTourEvent.EnableRoundTripFare) },
                        onRemove = { viewModel.onEvent(CreateTourEvent.RemoveRoundTripFare) },
                    )

                    FareConfigurationInput(
                        title = stringResource(R.string.outbound_fare_label),
                        value = uiState.outboundFare,
                        enabled = uiState.isOutboundFareEnabled,
                        canRemove = uiState.enabledFareCount > 1,
                        errorMessage = if (CreateTourField.OutboundFare in uiState.invalidFields) stringResource(R.string.fare_error) else null,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.OutboundFareChanged(it)) },
                        onEnable = { viewModel.onEvent(CreateTourEvent.EnableOutboundFare) },
                        onRemove = { viewModel.onEvent(CreateTourEvent.RemoveOutboundFare) },
                    )

                    FareConfigurationInput(
                        title = stringResource(R.string.return_fare_label),
                        value = uiState.returnFare,
                        enabled = uiState.isReturnFareEnabled,
                        canRemove = uiState.enabledFareCount > 1,
                        errorMessage = if (CreateTourField.ReturnFare in uiState.invalidFields) stringResource(R.string.fare_error) else null,
                        onValueChange = { viewModel.onEvent(CreateTourEvent.ReturnFareChanged(it)) },
                        onEnable = { viewModel.onEvent(CreateTourEvent.EnableReturnFare) },
                        onRemove = { viewModel.onEvent(CreateTourEvent.RemoveReturnFare) },
                    )
                }
            }
        }
    }
}
