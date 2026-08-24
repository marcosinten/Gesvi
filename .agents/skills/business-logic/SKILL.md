---
name: business-logic
description: Reglas sobre el flujo de dinero, asientos y reservas.
---

# Business Logic Skill

**REGLA DE ORO:** Nunca inventes reglas de negocio.

Antes de implementar cálculos de dinero o cambios de estado de asientos, debes:
1. Leer `docs/BUSINESS_RULES.md`.
2. Leer `docs/DATA_MODEL.md`.

**Conceptos clave:**
- Un **Booking** agrupa varios **Seats**.
- Un **Payment** se hace sobre un **Booking**, no sobre un Seat individual.
- Si se modifica un abono, debes recalcular el estado visual (`SeatStatus`) de todos los asientos de esa reserva.
