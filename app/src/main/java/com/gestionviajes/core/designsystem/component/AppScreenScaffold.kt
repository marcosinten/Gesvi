package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.gestionviajes.core.designsystem.theme.AppTheme

private val HeroHeaderShape = RoundedCornerShape(
    bottomStart = 36.dp,
    bottomEnd = 36.dp,
)

@Composable
fun AppScreenScaffold(
    title: String,
    headerIcon: ImageVector,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    headerIconContentDescription: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    navigateBackContentDescription: String? = null,
    showHeader: Boolean = true,
    snackbarHost: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (showHeader) {
                AppHeroHeader(
                    title = title,
                    icon = headerIcon,
                    eyebrow = eyebrow,
                    subtitle = subtitle,
                    iconContentDescription = headerIconContentDescription,
                    onNavigateBack = onNavigateBack,
                    navigateBackContentDescription = navigateBackContentDescription,
                )
            }
        },
        snackbarHost = snackbarHost,
        bottomBar = bottomBar,
        containerColor = MaterialTheme.colorScheme.background,
        content = content,
    )
}

/** Cabecera curva de Gesvi con un recorrido sutil como firma visual. */
@Composable
fun AppHeroHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    eyebrow: String? = null,
    subtitle: String? = null,
    iconContentDescription: String? = null,
    onNavigateBack: (() -> Unit)? = null,
    navigateBackContentDescription: String? = null,
) {
    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val extendedColors = AppTheme.extendedColors

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = HeroHeaderShape,
        color = extendedColors.brandSurface,
        contentColor = extendedColors.onBrandSurface,
        shadowElevation = dimensions.floatingElevation,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HeaderRouteDecoration(modifier = Modifier.matchParentSize())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                        ),
                    )
                    .padding(horizontal = spacing.large)
                    .padding(top = spacing.medium, bottom = spacing.extraLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            ) {
                if (onNavigateBack != null) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(dimensions.minimumTouchTarget),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = navigateBackContentDescription,
                            tint = extendedColors.onBrandSurface,
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                ) {
                    if (eyebrow != null) {
                        Text(
                            text = eyebrow,
                            color = extendedColors.onBrandSurface.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                    Text(
                        text = title,
                        modifier = Modifier.semantics { heading() },
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            color = extendedColors.onBrandSurface.copy(alpha = 0.86f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }

                Surface(
                    modifier = Modifier.size(dimensions.headerIconContainer),
                    shape = CircleShape,
                    color = extendedColors.accentSky,
                    contentColor = extendedColors.brandSurface,
                    shadowElevation = dimensions.buttonElevation,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = iconContentDescription,
                            modifier = Modifier.size(dimensions.iconLarge),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRouteDecoration(modifier: Modifier = Modifier) {
    val colors = AppTheme.extendedColors

    Canvas(modifier = modifier) {
        val route = Path().apply {
            moveTo(size.width * 0.42f, size.height * 0.08f)
            cubicTo(
                size.width * 0.58f,
                size.height * 0.18f,
                size.width * 0.58f,
                size.height * 0.82f,
                size.width * 0.86f,
                size.height * 0.76f,
            )
        }
        drawPath(
            path = route,
            color = colors.accentSky.copy(alpha = 0.2f),
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round),
        )
        drawCircle(
            color = colors.accentPink,
            radius = 6.dp.toPx(),
            center = Offset(size.width * 0.56f, size.height * 0.35f),
        )
        drawCircle(
            color = colors.accentYellow,
            radius = 7.dp.toPx(),
            center = Offset(size.width * 0.67f, size.height * 0.68f),
        )
        drawCircle(
            color = colors.accentGreen,
            radius = 5.dp.toPx(),
            center = Offset(size.width * 0.82f, size.height * 0.76f),
        )
    }
}
