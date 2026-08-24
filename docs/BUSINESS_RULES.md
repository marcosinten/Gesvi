# Reglas de Negocio

- **Asientos Múltiples:** Una sola reserva (Booking) puede incluir múltiples asientos (Seat).
- **Abonos Globales:** Los abonos (Payments) se aplican a la reserva total, no a asientos individuales.
- **Estado del Asiento:**
  - Si Suma(Abonos) == 0 -> `Reserved`
  - Si 0 < Suma(Abonos) < TarifaTotal -> `Partial`
  - Si Suma(Abonos) >= TarifaTotal -> `Paid`
- **Saldos:** El saldo pendiente de un viaje es la suma de las tarifas totales menos la suma de los abonos reales.
