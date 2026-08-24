package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gestionviajes.core.designsystem.theme.AppTheme

/** Etiqueta textual redondeada para acompañar un estado sin depender solo del color. */
@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    val spacing = AppTheme.spacing

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = spacing.medium,
                vertical = spacing.small,
            ),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
