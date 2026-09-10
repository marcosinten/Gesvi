package com.gestionviajes.feature.trips.presentation.screen

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gestionviajes.R
import com.gestionviajes.core.designsystem.component.AppButton
import com.gestionviajes.core.designsystem.component.AppButtonStyle
import com.gestionviajes.core.designsystem.component.AppCard
import com.gestionviajes.core.designsystem.component.AppIconBadge
import com.gestionviajes.core.designsystem.component.AppScreenScaffold
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.feature.trips.presentation.CreateTourEffect
import com.gestionviajes.feature.trips.presentation.CreateTourEvent
import com.gestionviajes.feature.trips.presentation.CreateTourField
import com.gestionviajes.feature.trips.presentation.CreateTourUiState
import com.gestionviajes.feature.trips.presentation.CreateTourViewModel
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlinx.coroutines.launch

private val SpanishLocale = Locale.forLanguageTag("es-ES")
private const val TimeWheelVisibleItemCount = 3

@Composable
fun CreateTourScreen(
    onNavigateBack: () -> Unit,
    onTourCreated: (Long) -> Unit,
    viewModel: CreateTourViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnTourCreated by rememberUpdatedState(onTourCreated)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            if (effect is CreateTourEffect.Created) currentOnTourCreated(effect.tourId)
        }
    }

    CreateTourContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEvent = viewModel::onEvent,
    )
}

@Composable
internal fun CreateTourContent(
    uiState: CreateTourUiState,
    onNavigateBack: () -> Unit,
    onEvent: (CreateTourEvent) -> Unit,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors

    AppScreenScaffold(
        eyebrow = stringResource(R.string.app_name).uppercase(SpanishLocale),
        title = stringResource(R.string.create_tour_title),
        subtitle = stringResource(R.string.create_tour_subtitle),
        headerIcon = Icons.Rounded.Add,
        onNavigateBack = onNavigateBack,
        navigateBackContentDescription = stringResource(R.string.navigate_back),
        bottomBar = {
            CreateTourSaveBar(
                isSaving = uiState.isSaving,
                hasValidationErrors = uiState.invalidFields.isNotEmpty(),
                saveFailed = uiState.saveFailed,
                onSubmit = { onEvent(CreateTourEvent.Submit) },
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
                        onValueChange = {
                            onEvent(CreateTourEvent.DestinationChanged(it))
                        },
                        label = stringResource(R.string.destination_label),
                        errorMessage = if (CreateTourField.Destination in uiState.invalidFields) {
                            stringResource(R.string.destination_required_error)
                        } else {
                            null
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    FormTextField(
                        value = uiState.supervisor,
                        onValueChange = {
                            onEvent(CreateTourEvent.SupervisorChanged(it))
                        },
                        label = stringResource(R.string.supervisor_label),
                        errorMessage = if (CreateTourField.Supervisor in uiState.invalidFields) {
                            stringResource(R.string.supervisor_required_error)
                        } else {
                            null
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next,
                        ),
                    )
                }
            }

            item {
                DateAndTimeSection(uiState = uiState, onEvent = onEvent)
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
                        onValueChange = {
                            onEvent(CreateTourEvent.SeatCountChanged(it))
                        },
                        label = stringResource(R.string.seat_count_label),
                        errorMessage = if (CreateTourField.SeatCount in uiState.invalidFields) {
                            stringResource(R.string.seat_count_error)
                        } else {
                            null
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    MoneyTextField(
                        value = uiState.freight,
                        onValueChange = { onEvent(CreateTourEvent.FreightChanged(it)) },
                        label = stringResource(R.string.freight_label),
                        errorMessage = if (CreateTourField.Freight in uiState.invalidFields) {
                            stringResource(R.string.freight_error)
                        } else {
                            null
                        },
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
                        errorMessage = if (CreateTourField.RoundTripFare in uiState.invalidFields) {
                            stringResource(R.string.fare_error)
                        } else {
                            null
                        },
                        onValueChange = { onEvent(CreateTourEvent.RoundTripFareChanged(it)) },
                        onEnable = { onEvent(CreateTourEvent.EnableRoundTripFare) },
                        onRemove = { onEvent(CreateTourEvent.RemoveRoundTripFare) },
                    )

                    FareConfigurationInput(
                        title = stringResource(R.string.outbound_fare_label),
                        value = uiState.outboundFare,
                        enabled = uiState.isOutboundFareEnabled,
                        canRemove = uiState.enabledFareCount > 1,
                        errorMessage = if (CreateTourField.OutboundFare in uiState.invalidFields) {
                            stringResource(R.string.fare_error)
                        } else {
                            null
                        },
                        onValueChange = { onEvent(CreateTourEvent.OutboundFareChanged(it)) },
                        onEnable = { onEvent(CreateTourEvent.EnableOutboundFare) },
                        onRemove = { onEvent(CreateTourEvent.RemoveOutboundFare) },
                    )

                    FareConfigurationInput(
                        title = stringResource(R.string.return_fare_label),
                        value = uiState.returnFare,
                        enabled = uiState.isReturnFareEnabled,
                        canRemove = uiState.enabledFareCount > 1,
                        errorMessage = if (CreateTourField.ReturnFare in uiState.invalidFields) {
                            stringResource(R.string.fare_error)
                        } else {
                            null
                        },
                        onValueChange = { onEvent(CreateTourEvent.ReturnFareChanged(it)) },
                        onEnable = { onEvent(CreateTourEvent.EnableReturnFare) },
                        onRemove = { onEvent(CreateTourEvent.RemoveReturnFare) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun DateAndTimeSection(
    uiState: CreateTourUiState,
    onEvent: (CreateTourEvent) -> Unit,
) {
    val spacing = AppTheme.spacing
    val colors = AppTheme.extendedColors
    val hasDateError = CreateTourField.Date in uiState.invalidFields
    val monthOptions = remember {
        (1..12).map { monthNumber ->
            val monthName = Month.of(monthNumber)
                .getDisplayName(TextStyle.FULL, SpanishLocale)
                .replaceFirstChar { character -> character.titlecase(SpanishLocale) }
            monthNumber to monthName
        }
    }
    val yearOptions = remember(uiState.minimumYear, uiState.maximumYear) {
        (uiState.minimumYear..uiState.maximumYear).map { year -> year to year.toString() }
    }

    FormSection(
        title = stringResource(R.string.tour_departure_section),
        icon = Icons.Rounded.CalendarMonth,
        iconContainerColor = colors.accentYellowContainer,
        iconContentColor = colors.warning,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            verticalAlignment = Alignment.Top,
        ) {
            FormTextField(
                value = uiState.day,
                onValueChange = { onEvent(CreateTourEvent.DayChanged(it)) },
                label = stringResource(R.string.day_label),
                modifier = Modifier.weight(1f),
                isError = hasDateError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
            )
            SelectorDropdown(
                selectedValue = uiState.month,
                options = monthOptions,
                onValueSelected = { onEvent(CreateTourEvent.MonthChanged(it)) },
                label = stringResource(R.string.month_label),
                modifier = Modifier.weight(2f),
                isError = hasDateError,
            )
        }
        SelectorDropdown(
            selectedValue = uiState.year,
            options = yearOptions,
            onValueSelected = { onEvent(CreateTourEvent.YearChanged(it)) },
            label = stringResource(R.string.year_label),
            modifier = Modifier.fillMaxWidth(),
            isError = hasDateError,
        )
        if (hasDateError) {
            FieldError(text = stringResource(R.string.date_error))
        }
        TimeSelector(
            hour = uiState.hour,
            minute = uiState.minute,
            isError = CreateTourField.Time in uiState.invalidFields,
            onTimeSelected = { hour, minute ->
                onEvent(CreateTourEvent.TimeChanged(hour, minute))
            },
            title = stringResource(R.string.departure_time_label),
        )
        VenidaTimeSection(uiState = uiState, onEvent = onEvent)
    }
}

@Composable
internal fun VenidaTimeSection(
    uiState: CreateTourUiState,
    onEvent: (CreateTourEvent) -> Unit,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text = stringResource(R.string.return_time_toggle_label),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
        )
        Switch(
            checked = uiState.isVenidaDefined,
            onCheckedChange = { onEvent(CreateTourEvent.VenidaDefinedChanged(it)) },
            modifier = Modifier.heightIn(min = dimensions.minimumTouchTarget),
        )
    }
    if (uiState.isVenidaDefined) {
        TimeSelector(
            hour = uiState.venidaHour,
            minute = uiState.venidaMinute,
            isError = CreateTourField.ReturnTime in uiState.invalidFields,
            onTimeSelected = { hour, minute ->
                onEvent(CreateTourEvent.VenidaTimeChanged(hour, minute))
            },
            title = stringResource(R.string.return_time_label),
            errorMessage = stringResource(R.string.return_time_error),
        )
    }
}

@Composable
internal fun FormSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconContainerColor: Color,
    iconContentColor: Color,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = AppTheme.spacing

    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(spacing.large),
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                AppIconBadge(
                    imageVector = icon,
                    contentDescription = null,
                    containerColor = iconContainerColor,
                    contentColor = iconContentColor,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (description != null) {
                        Text(
                            text = description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            content()
        }
    }
}

@Composable
internal fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    isError: Boolean = errorMessage != null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    prefix: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        isError = isError,
        supportingText = errorMessage?.let { message ->
            { Text(message) }
        },
        keyboardOptions = keyboardOptions,
        prefix = prefix,
        shape = MaterialTheme.shapes.large,
    )
}

@Composable
internal fun MoneyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
) {
    FormTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        errorMessage = errorMessage,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next,
        ),
        prefix = { Text(stringResource(R.string.currency_symbol)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun <T> SelectorDropdown(
    selectedValue: T,
    options: List<Pair<T, String>>,
    onValueSelected: (T) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selectedValue }?.second.orEmpty()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge,
            singleLine = true,
            isError = isError,
            shape = MaterialTheme.shapes.large,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.second,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onValueSelected(option.first)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
internal fun TimeSelector(
    hour: Int,
    minute: Int,
    isError: Boolean,
    onTimeSelected: (Int, Int) -> Unit,
    title: String = stringResource(R.string.time_label),
    errorMessage: String = stringResource(R.string.time_error),
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val hourValues = remember { (0..23).toList() }
    val minuteValues = remember { (0..59).toList() }
    val hourLabel = stringResource(R.string.hour_wheel_label)
    val minuteLabel = stringResource(R.string.minute_wheel_label)

    Column(
        modifier = Modifier.semantics {
            stateDescription = String.format(SpanishLocale, "%02d:%02d", hour, minute)
        },
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = hourLabel,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
            Box(modifier = Modifier.size(dimensions.minimumTouchTarget))
            Text(
                text = minuteLabel,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TimeWheel(
                values = hourValues,
                selectedValue = hour,
                contentDescription = hourLabel,
                onValueSelected = { selectedHour ->
                    onTimeSelected(selectedHour, minute)
                },
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier.size(dimensions.minimumTouchTarget),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ":",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
            TimeWheel(
                values = minuteValues,
                selectedValue = minute,
                contentDescription = minuteLabel,
                onValueSelected = { selectedMinute ->
                    onTimeSelected(hour, selectedMinute)
                },
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = stringResource(R.string.time_wheel_help),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
        )
        if (isError) {
            FieldError(text = errorMessage)
        }
    }
}

@Composable
internal fun TimeWheel(
    values: List<Int>,
    selectedValue: Int,
    contentDescription: String,
    onValueSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions = AppTheme.dimensions
    val selectedIndex = values.indexOf(selectedValue).coerceAtLeast(0)
    val wheelItems = remember(values) { listOf<Int?>(null) + values + null }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val scope = rememberCoroutineScope()
    val centeredItemIndex by remember(listState) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (
                layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset
            ) / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - viewportCenter)
            }?.index
        }
    }
    val centeredValue = centeredItemIndex?.let { index -> wheelItems.getOrNull(index) }

    LaunchedEffect(listState.isScrollInProgress, centeredValue) {
        if (!listState.isScrollInProgress && centeredValue != null && centeredValue != selectedValue) {
            onValueSelected(centeredValue)
        }
    }

    LaunchedEffect(selectedValue) {
        if (!listState.isScrollInProgress && centeredValue != selectedValue) {
            listState.scrollToItem(selectedIndex)
        }
    }

    Surface(
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
            stateDescription = selectedValue.toString().padStart(2, '0')
        },
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.minimumTouchTarget * TimeWheelVisibleItemCount)
                .selectableGroup(),
            horizontalAlignment = Alignment.CenterHorizontally,
            flingBehavior = flingBehavior,
        ) {
            itemsIndexed(
                items = wheelItems,
                key = { index, _ -> index },
            ) { index, value ->
                if (value == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.minimumTouchTarget),
                    )
                } else {
                    val isSelected = value == (centeredValue ?: selectedValue)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.minimumTouchTarget)
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = {
                                    scope.launch {
                                        listState.animateScrollToItem(index - 1)
                                    }
                                },
                            )
                            .semantics { selected = isSelected },
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = value.toString().padStart(2, '0'),
                                style = if (isSelected) {
                                    MaterialTheme.typography.titleLarge
                                } else {
                                    MaterialTheme.typography.bodyLarge
                                },
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FareConfigurationInput(
    title: String,
    value: String,
    enabled: Boolean,
    canRemove: Boolean,
    errorMessage: String?,
    onValueChange: (String) -> Unit,
    onEnable: () -> Unit,
    onRemove: () -> Unit,
) {
    val dimensions = AppTheme.dimensions

    if (!enabled) {
        AddFareButton(text = title, onClick = onEnable)
        return
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )
            TextButton(
                onClick = onRemove,
                enabled = canRemove,
                modifier = Modifier.heightIn(min = dimensions.minimumTouchTarget),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Remove,
                    contentDescription = stringResource(R.string.remove_fare_action),
                )
                Text(stringResource(R.string.remove_fare_action))
            }
        }
        MoneyTextField(
            value = value,
            onValueChange = onValueChange,
            label = stringResource(R.string.fare_price_label, title),
            errorMessage = errorMessage,
        )
    }
}

@Composable
internal fun AddFareButton(text: String, onClick: () -> Unit) {
    AppButton(
        text = stringResource(R.string.add_fare_action, text),
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        style = AppButtonStyle.Outlined,
        leadingIcon = {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        },
    )
}

@Composable
internal fun FieldError(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
internal fun CreateTourSaveBar(
    isSaving: Boolean,
    hasValidationErrors: Boolean,
    saveFailed: Boolean,
    onSubmit: () -> Unit,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions

    Surface(
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = dimensions.floatingElevation,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = spacing.large, vertical = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            when {
                hasValidationErrors -> SaveMessage(
                    text = stringResource(R.string.create_tour_validation_summary),
                )
                saveFailed -> SaveMessage(
                    text = stringResource(R.string.create_tour_save_error),
                )
            }
            AppButton(
                text = if (isSaving) {
                    stringResource(R.string.creating_tour_action)
                } else {
                    stringResource(R.string.create_tour_button)
                },
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                leadingIcon = {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(dimensions.iconMedium),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = spacing.extraSmall,
                        )
                    } else {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    }
                },
            )
        }
    }
}

@Composable
private fun SaveMessage(text: String) {
    val spacing = AppTheme.spacing

    Row(
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.dimensions.iconMedium),
            tint = MaterialTheme.colorScheme.error,
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
