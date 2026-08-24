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
- Cambiar una tarifa actualiza los totales, saldos, estados y cuentas relacionados, pero no los `PaymentRecord`.
- `PaymentRecord belongs to Booking`, nunca a `Seat` ni a `BookingSeat`.
- Un `Booking` conserva `1..N` asientos; no se puede eliminar su último `BookingSeat`.
- Quitar un asiento conserva los abonos y recalcula el Booking sin distribuirlos.
- El cambio de asiento solo tiene como destino un asiento `EMPTY`.
- Los estados se calculan automáticamente para el Booking completo.
- El flete pertenece al Tour, se usa en cuentas y no aparece en la pantalla operativa de asientos.
- Gesvi registra dinero declarado; no procesa pagos ni transacciones bancarias.
- Mapa, listado, PDF y cuentas derivan del mismo estado de repositorio/dominio.
