---
name: android-feature
description: Usar al crear o modificar features Android, ViewModels, casos de uso, repositorios, Room, Hilt o flujos de datos de Gesvi.
---

# Android Feature Skill

Antes de modificar arquitectura o flujo de datos, lee `docs/ARCHITECTURE.md`. Si la feature toca negocio o UI, carga también `business-logic` y/o `mobile-ux` con sus documentos obligatorios.

Mantén el flujo:

```text
Compose
  -> Event
  -> ViewModel
  -> Use Case
  -> Repository interface
  -> Repository implementation
  -> Data source / Room DAO
```

Prohibiciones estrictas:

```text
Composable -> DAO
ViewModel  -> DAO
Composable -> Room Entity
ViewModel  -> Room Entity
Domain     -> Room
Domain     -> Design System
```

Además:

- Usa `ViewModel` estándar inyectado por Hilt, no `AndroidViewModel`.
- No pases `NavController` a Composables profundos; expón lambdas de navegación.
- Mantén Room entities y mappers dentro de Data.
- Mantén reglas e interfaces de repositorio dentro de Domain.
- Expón `StateFlow`/UI State inmutable desde el ViewModel.
- Mantén operación offline; no añadas una dependencia de red para completar el flujo.
- Conserva el único módulo Gradle `:app` salvo decisión explícita.

Fuente única de verdad:

- `BookingRepository` y el estado de dominio son autoritativos para reservas, ocupación y abonos.
- Seat Map, Passenger List, Printable Manifest y Accounts son proyecciones de ese mismo estado.
- No crees tablas, repositorios ni caches de negocio independientes por pantalla.
- Solo el estado transitorio de UI, como selección o texto en edición, puede mantenerse localmente.
