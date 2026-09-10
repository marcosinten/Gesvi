package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gestionviajes.core.designsystem.theme.AppTheme

/** Contenedor ilustrativo para iconos, disponible en tamaño normal o prominente. */
@Composable
fun AppIconBadge(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    prominent: Boolean = false,
    containerColor: Color = AppTheme.extendedColors.accentSkyContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    val dimensions = AppTheme.dimensions
    val containerSize = if (prominent) {
        dimensions.illustrationContainer
    } else {
        dimensions.minimumTouchTarget
    }
    val iconSize = if (prominent) dimensions.iconLarge else dimensions.iconMedium

    Surface(
        modifier = modifier.size(containerSize),
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = if (prominent) dimensions.buttonElevation else dimensions.cardElevation,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
            )
        }
    }
}
