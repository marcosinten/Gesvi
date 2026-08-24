# Gesvi

## Qué Es

Gesvi es una aplicación Android para administrar Tours de pasajeros. Digitaliza el trabajo que el encargado realiza normalmente con un listado numerado y un cuaderno, sin obligarlo a cambiar de forma radical su modelo mental.

Está dirigida principalmente a personas adultas con poca familiaridad tecnológica. Por ello prioriza claridad, pocos pasos, números de asiento visibles y funcionamiento local confiable.

## Alcance Del Producto

Gesvi permitirá:

- crear y consultar Tours registrados;
- registrar su fecha y hora;
- definir el número de asientos;
- registrar el valor del flete del carro;
- editar posteriormente esos datos desde el módulo de Tours;
- configurar los tipos de pasaje permitidos y sus precios;
- administrar reservas o ventas de uno o varios asientos;
- registrar el responsable común de una reserva;
- liberar cualquier asiento de una reserva sin eliminar su historial;
- registrar uno o varios abonos;
- consultar lo abonado, el saldo pendiente y el saldo a favor;
- cambiar una asignación a otro asiento disponible;
- visualizar un mapa de asientos;
- visualizar el listado numerado de pasajeros;
- consultar las cuentas del Tour;
- generar el listado de pasajeros en PDF.

Un Tour no tiene ruta, origen ni destino. Los datos obligatorios y las restricciones para editar la cantidad de asientos se definen en `BUSINESS_RULES.md`.

## Límites

- Gesvi no procesa pagos.
- Gesvi no ejecuta transacciones bancarias ni confirma que el dinero exista fuera de la aplicación.
- La aplicación únicamente registra cuánto dinero el encargado declara haber recibido.
- La aplicación registra saldos a favor, pero no procesa devoluciones de dinero.
- No se requiere registrar el nombre individual de cada pasajero cuando una persona es responsable de varios asientos.
- No hay backend, sincronización cloud ni funciones de inteligencia artificial en el alcance actual.

## Operación Offline

La operación principal debe funcionar sin conexión. Tours, reservas, asignaciones, abonos, saldos y consultas se conservan localmente. Una posible fuente remota futura no puede convertirse en requisito para las tareas diarias ni cambiar el contrato que consume la UI.

## Fuentes De Especificación

- Reglas del negocio: `BUSINESS_RULES.md`.
- Modelo conceptual: `DATA_MODEL.md`.
- Experiencia de usuario: `UX_PRINCIPLES.md`.
- Representación del listado: `PASSENGER_MANIFEST.md`.
- Arquitectura: `ARCHITECTURE.md`.
