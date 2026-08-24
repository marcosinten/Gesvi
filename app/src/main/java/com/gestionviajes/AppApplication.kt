package com.gestionviajes

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point.
 *
 * @HiltAndroidApp genera el componente Hilt raíz que permite la inyección
 * de dependencias en toda la aplicación.
 *
 * Agrega aquí inicializaciones globales (analytics, logging, etc.)
 * sin lógica de negocio — solo infraestructura.
 */
@HiltAndroidApp
class AppApplication : Application()
