package com.gestionviajes.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.gestionviajes.core.designsystem.theme.AppTheme

/** Composición temporal coherente para destinos que aún no tienen contenido funcional. */
@Composable
fun AppPlaceholderScreen(
    title: String,
    message: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    navigateBackContentDescription: String? = null,
    eyebrow: String? = null,
    showHeader: Boolean = true,
    bottomBar: @Composable () -> Unit = {},
) {
    if (showHeader) {
        AppScreenScaffold(
            title = title,
            headerIcon = icon,
            modifier = modifier,
            eyebrow = eyebrow,
            onNavigateBack = onNavigateBack,
            navigateBackContentDescription = navigateBackContentDescription,
            bottomBar = bottomBar,
        ) { innerPadding ->
            PlaceholderContent(
                title = title,
                message = message,
                icon = icon,
                innerPadding = innerPadding,
            )
        }
    } else {
        Scaffold(
            modifier = modifier,
            bottomBar = bottomBar,
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            PlaceholderContent(
                title = title,
                message = message,
                icon = icon,
                innerPadding = innerPadding,
            )
        }
    }
}

@Composable
private fun PlaceholderContent(
    title: String,
    message: String,
    icon: ImageVector,
    innerPadding: PaddingValues,
) {
    val spacing = AppTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AppEmptyState(
            title = title,
            message = message,
            icon = icon,
        )
    }
}
