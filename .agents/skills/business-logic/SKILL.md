---
name: business-logic
description: Usar ante cambios de Tours, tarifas, Booking, BookingSeat, abonos, saldos, cuentas o listado de pasajeros.
---

# Business Logic Skill

Antes de modificar comportamiento:

1. Lee siempre `docs/BUSINESS_RULES.md` y `docs/DATA_MODEL.md`.
2. Lee `docs/PRODUCT.md` al cambiar Tours o alcance.
3. Lee `docs/PASSENGER_MANIFEST.md` al cambiar mapa, listado o PDF.

Nunca inventes reglas. No rompas estas invariantes:

- Un Tour no tiene ruta, origen ni destino. Sus precios nunca se hardcodean.
- Reducir asientos solo es válido si todos los que desaparecerían están `EMPTY`.
- `Ida y vuelta` siempre existe; `Solo ida` y `Solo venida` son opcionales y no hay tipos personalizados.
- Cambiar tarifas, tipos de pasaje o asientos recalcula total, recibido, saldo pendiente, saldo a favor y estado.
- `PaymentRecord belongs to Booking`, nunca a `Seat` ni a `BookingSeat`.
- Los `PaymentRecord` históricos nunca se reescriben al recalcular un `Booking`.
- Un `Booking` puede terminar sin asientos activos y conservarse como historial.
- Liberar un asiento lo devuelve a `EMPTY`, conserva los abonos y recalcula el `Booking` sin distribuirlos.
- El cambio de asiento solo tiene como destino un asiento `EMPTY`.
- Los estados se calculan automáticamente para el Booking completo.
- El flete pertenece al Tour, se usa en cuentas y no aparece en la pantalla operativa de asientos.
- Gesvi registra dinero y saldos a favor declarados; no procesa pagos, transacciones ni devoluciones.
- Mapa, listado, PDF y cuentas derivan del mismo estado de repositorio/dominio.
