package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.core.designsystem.token.SeatStatus

/** Celda táctil con número prominente y color semántico centralizado. */
@Composable
fun SeatCell(
    number: String,
    status: SeatStatus,
    statusLabel: String,
    seatContentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    selectable: Boolean = true,
    enabled: Boolean = true,
) {
    val colors = AppTheme.extendedColors
    val dimensions = AppTheme.dimensions
    val backgroundColor = when (status) {
        SeatStatus.Empty -> colors.seatEmpty
        SeatStatus.Reserved -> colors.seatReserved
        SeatStatus.Partial -> colors.seatPartial
        SeatStatus.Paid -> colors.seatPaid
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .sizeIn(
                minWidth = dimensions.minimumTouchTarget,
                minHeight = dimensions.seatCell,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = seatContentDescription
                stateDescription = statusLabel
                if (selectable) this.selected = selected
                role = Role.Button
            },
        shape = MaterialTheme.shapes.small,
        color = Color.Transparent,
        contentColor = colors.onSeat,
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = AppTheme.spacing.extraSmall,
                        vertical = AppTheme.spacing.extraSmall,
                    ),
                shape = MaterialTheme.shapes.small,
                color = backgroundColor,
                contentColor = colors.onSeat,
                border = BorderStroke(
                    width = if (selected) {
                        AppTheme.spacing.extraSmall
                    } else {
                        AppTheme.spacing.extraSmall * 0.5f
                    },
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    },
                ),
                shadowElevation = dimensions.buttonElevation,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = number,
                        modifier = Modifier.clearAndSetSemantics {},
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(AppTheme.spacing.extraSmall)
                    .height(dimensions.iconMedium),
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.outlineVariant,
                content = {},
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(AppTheme.spacing.extraSmall)
                    .height(dimensions.iconMedium),
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.outlineVariant,
                content = {},
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.62f)
                    .height(AppTheme.spacing.extraSmall),
                shape = MaterialTheme.shapes.extraSmall,
                color = MaterialTheme.colorScheme.outlineVariant,
                content = {},
            )
        }
    }
}
