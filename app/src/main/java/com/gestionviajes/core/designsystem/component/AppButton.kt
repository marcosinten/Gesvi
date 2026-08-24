package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gestionviajes.core.designsystem.theme.AppTheme

enum class AppButtonStyle {
    Primary,
    Tonal,
    Outlined,
}

enum class AppButtonSize {
    Default,
    Compact,
}

/** Acción reconocible con variantes visuales y tamaños del mismo sistema. */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle.Primary,
    size: AppButtonSize = AppButtonSize.Default,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val shape = MaterialTheme.shapes.large
    val contentPadding = PaddingValues(
        horizontal = if (size == AppButtonSize.Compact) spacing.medium else spacing.large,
        vertical = if (size == AppButtonSize.Compact) spacing.extraSmall else spacing.small,
    )
    val content: @Composable RowScope.() -> Unit = {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(modifier = Modifier.width(spacing.small))
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
    val sizedModifier = modifier.heightIn(
        min = if (size == AppButtonSize.Compact) {
            dimensions.minimumTouchTarget
        } else {
            dimensions.buttonHeight
        },
    )

    when (style) {
        AppButtonStyle.Primary -> Button(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = enabled,
            shape = shape,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = dimensions.buttonElevation,
                pressedElevation = dimensions.cardElevation,
            ),
            contentPadding = contentPadding,
            content = content,
        )

        AppButtonStyle.Tonal -> FilledTonalButton(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = enabled,
            shape = shape,
            contentPadding = contentPadding,
            content = content,
        )

        AppButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = sizedModifier,
            enabled = enabled,
            shape = shape,
            border = BorderStroke(
                width = spacing.extraSmall * 0.5f,
                color = MaterialTheme.colorScheme.primary,
            ),
            contentPadding = contentPadding,
            content = content,
        )
    }
}
