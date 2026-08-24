---
name: business-logic
description: Usar ante cambios de Tours, tarifas, reservas, ventas, asientos, cambios o liberaciones de asiento, abonos, saldos, cuentas, listado o PDF.
---

# Business Logic Skill

Antes de modificar comportamiento:

1. Lee siempre `docs/BUSINESS_RULES.md` y `docs/DATA_MODEL.md`.
2. Lee `docs/PRODUCT.md` al cambiar Tours o alcance.
3. Lee `docs/PASSENGER_MANIFEST.md` al cambiar mapa, listado o PDF.

Nunca inventes reglas. No rompas estas invariantes:

- Un Tour no tiene ruta, origen ni destino. Sus precios nunca se hardcodean.
- Reducir asientos solo es válido si todos los que desaparecerían están `EMPTY`.
- `Reservar` inicia sin dinero; `Vender` exige un monto inicial mayor a `0`.
- Cambiar tarifas, tipos de pasaje o asientos recalcula los valores derivados del `Booking`.
- `PaymentRecord belongs to Booking`, nunca a `Seat` ni a `BookingSeat`.
- Los `PaymentRecord` históricos nunca se reescriben al recalcular un `Booking`.
- Un `Booking` puede terminar sin asientos activos y conservarse como historial.
- Liberar un asiento lo devuelve a `EMPTY`, conserva los abonos y recalcula el `Booking` sin distribuirlos.
- El cambio de asiento solo tiene como destino un asiento `EMPTY`.
- Gesvi registra dinero y saldos a favor declarados; no procesa pagos, transacciones ni devoluciones.
- Mapa, listado, cobros, PDF y cuentas derivan del mismo estado de repositorio/dominio.
