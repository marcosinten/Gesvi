# IA Futura

## Estado

Gesvi no incluye inteligencia artificial en el alcance actual. Este documento solo reserva límites arquitectónicos para una posible evolución.

No se deben añadir todavía:

- dependencias o SDKs de IA;
- modelos locales;
- llamadas a servicios de IA;
- backend para IA;
- telemetría o envío de datos de pasajeros;
- pantallas o flujos de IA.

## Extensión Posible

En el futuro podrían existir paquetes lógicos como:

```text
analytics/
ai/
```

con contratos de Domain, por ejemplo:

```text
ProfitAnalyzer
TripInsightsProvider
DemandPredictor
```

La UI y los casos de uso deben depender de contratos estables, no de un proveedor concreto:

```text
ProfitAnalyzer
 |-- BasicProfitAnalyzer
 `-- AIProfitAnalyzer
```

Hilt seleccionaría la implementación. La UI no debería saber si el resultado procede de reglas básicas o de IA.

## Condiciones Antes De Implementar

Cualquier trabajo futuro requiere una solicitud explícita y decisiones humanas sobre:

- problema concreto y beneficio esperado;
- datos autorizados para análisis;
- privacidad y consentimiento;
- funcionamiento offline y comportamiento cuando no haya red;
- explicabilidad y revisión humana;
- coste, proveedor y mantenimiento;
- pruebas y alternativa determinista.

La IA nunca debe convertirse en fuente de verdad para reservas, abonos, asientos o cuentas. Los datos confirmados continúan en los repositorios de dominio y las recomendaciones deben distinguirse claramente de los hechos registrados.
