---
name: business-logic
description: Usar ante cambios de Trip, Booking, Seat, FareType, PaymentRecord, saldos, estados económicos o cuentas.
---

# Business Logic Skill

Activa esta skill ante cualquier cambio de:

- `Trip`;
- `Booking`;
- `Seat` o `BookingSeat`;
- `FareType`;
- `PaymentRecord`;
- abonos, saldos, estados económicos o cuentas.

Antes de modificar comportamiento:

1. Lee `docs/BUSINESS_RULES.md`.
2. Lee `docs/DATA_MODEL.md`.

Regla de oro: nunca inventes reglas de negocio. Si los documentos dejan un caso abierto, solicita confirmación humana.

Comprobaciones obligatorias:

- `PaymentRecord belongs to Booking`.
- Un `PaymentRecord` nunca pertenece a `Seat` ni a `BookingSeat`.
- Un `Booking` agrupa de `1..N` asientos y puede mezclar tipos de pasaje.
- Los abonos y el saldo se calculan para el `Booking` completo.
- Todos los asientos de una reserva comparten `RESERVED`, `PARTIAL` o `PAID`.
- El cambio de asiento solo tiene como destino un asiento `EMPTY`.
- La cancelación simple solo aplica a una reserva sin ningún `PaymentRecord`.
- Solo existen `Ida y vuelta`, `Solo ida` y `Solo venida`.
- Gesvi registra dinero declarado; no procesa pagos ni transacciones bancarias.
- Mapa, listado, impresión y cuentas derivan del mismo estado de repositorio/dominio.

No persistas estados económicos calculables ni distribuyas automáticamente un abono entre asientos.
