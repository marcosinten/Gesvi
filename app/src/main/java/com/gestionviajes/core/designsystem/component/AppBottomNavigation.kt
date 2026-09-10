package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.gestionviajes.core.designsystem.theme.AppTheme

@Immutable
data class AppBottomNavigationItem(
    val label: String,
    val icon: ImageVector,
)

/** Dock curvo con iconos y etiquetas siempre visibles. */
@Composable
fun AppBottomNavigation(
    items: List<AppBottomNavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    val spacing = AppTheme.spacing
    val dimensions = AppTheme.dimensions
    val colors = AppTheme.extendedColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                ),
            )
            .padding(horizontal = spacing.medium, vertical = spacing.small),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = colors.brandSurface,
            contentColor = colors.onBrandSurface,
            shadowElevation = dimensions.floatingElevation,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = dimensions.bottomBarHeight)
                    .selectableGroup()
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                items.forEachIndexed { index, item ->
                    val selected = index == selectedIndex
                    val iconColor = if (selected) {
                        colors.brandSurface
                    } else {
                        colors.onBrandSurface.copy(alpha = 0.78f)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .selectable(
                                selected = selected,
                                role = Role.Tab,
                                onClick = { onItemSelected(index) },
                            )
                            .padding(vertical = spacing.extraSmall),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dimensions.bottomBarIconContainer)
                                .background(
                                    color = if (selected) colors.accentSky else Color.Transparent,
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = null,
                                modifier = Modifier.size(dimensions.iconMedium),
                                tint = iconColor,
                            )
                        }
                        Text(
                            text = item.label,
                            color = colors.onBrandSurface,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
