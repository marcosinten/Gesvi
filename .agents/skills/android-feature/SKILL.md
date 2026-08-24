---
name: android-feature
description: Arquitectura y flujo de datos para nuevas funcionalidades.
---

# Android Feature Skill

Todas las features de esta app siguen este flujo unidireccional:

`Compose Screen` -> `Event` -> `ViewModel` -> `UseCase` -> `Repository Interface` -> `Repository Impl` -> `Room DAO`

**PROHIBICIONES ESTRICTAS:**
- NO accedas a DAOs ni Entities desde el ViewModel o la UI.
- NO uses `AndroidViewModel` (usa `ViewModel` estándar inyectado por Hilt).
- NO pases `NavController` a los Composables profundos (usa lambdas `onNavigate`).
- NO guardes estado localmente en el mapa de asientos. Debe ser reactivo (`Flow`) derivado de la base de datos.
