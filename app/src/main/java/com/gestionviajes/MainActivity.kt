package com.gestionviajes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gestionviajes.core.designsystem.theme.AppTheme
import com.gestionviajes.core.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

/**
 * Única Activity de la aplicación.
 *
 * Responsabilidades:
 * - Inicializar edge-to-edge
 * - Aplicar [AppTheme] como raíz de theming
 * - Montar el grafo de navegación [AppNavGraph]
 *
 * No debe contener ninguna lógica de negocio ni estado de pantalla.
 * Todo el estado vive en ViewModels; todo el UI en Composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                AppNavGraph()
            }
        }
    }
}
