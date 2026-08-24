# Instrucciones para Agentes (Codex/IA)

Este proyecto sigue una arquitectura **Clean + MVVM** fuertemente opinionada.
Antes de escribir cualquier código, debes cargar y leer las **skills** ubicadas en `.agents/skills/`.

## Reglas fundamentales
1. **NO mezclar capas:** La UI no habla con Room. Room no habla con la UI.
2. **Fuente de verdad única:** `SeatMap`, `PassengerList` y `Accounts` se calculan a partir del estado de `BookingRepository`. No tienen estado propio en base de datos.
3. **Local-first:** Todo debe funcionar sin conexión.
4. **Diseño:** Todo color y forma debe salir de `core/designsystem/theme`. NO hardcodear colores HEX en los Composables.

## Documentación obligatoria de lectura
Si vas a modificar reglas de negocio, lee:
- `docs/BUSINESS_RULES.md`
- `docs/DATA_MODEL.md`

Si vas a modificar UI, lee:
- `docs/UX_PRINCIPLES.md`
- `docs/DESIGN_SYSTEM.md`
