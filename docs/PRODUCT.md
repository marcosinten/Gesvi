# Gesvi

## Qué Es

Gesvi es una aplicación Android para administrar viajes de pasajeros. Digitaliza el trabajo que el encargado realiza normalmente con un listado numerado y un cuaderno, sin obligarlo a cambiar de forma radical su modelo mental.

Está dirigida principalmente a personas adultas con poca familiaridad tecnológica. Por ello prioriza claridad, pocos pasos, números de asiento visibles y funcionamiento local confiable.

## Alcance Del Producto

Gesvi permitirá:

- crear un viaje;
- registrar su fecha y hora;
- definir el número de asientos;
- registrar el valor del flete del carro;
- configurar los tipos de pasaje permitidos y sus precios;
- administrar reservas o ventas de uno o varios asientos;
- registrar el responsable común de una reserva;
- registrar uno o varios abonos;
- consultar lo abonado y el saldo pendiente;
- cambiar una asignación a otro asiento disponible;
- visualizar un mapa de asientos;
- visualizar el listado numerado de pasajeros;
- consultar las cuentas del viaje;
- generar una versión imprimible del listado de pasajeros.

Un viaje no requiere una ruta, origen ni destino. Los datos obligatorios del negocio se definen en `BUSINESS_RULES.md`.

## Límites

- Gesvi no procesa pagos.
- Gesvi no ejecuta transacciones bancarias ni confirma que el dinero exista fuera de la aplicación.
- La aplicación únicamente registra cuánto dinero el encargado declara haber recibido.
- No se requiere registrar el nombre individual de cada pasajero cuando una persona es responsable de varios asientos.
- No hay backend, sincronización cloud ni funciones de inteligencia artificial en el alcance actual.

## Operación Offline

La operación principal debe funcionar sin conexión. Viajes, reservas, asignaciones, abonos, saldos y consultas se conservan localmente. Una posible fuente remota futura no puede convertirse en requisito para las tareas diarias ni cambiar el contrato que consume la UI.

## Fuentes De Especificación

- Reglas del negocio: `BUSINESS_RULES.md`.
- Modelo conceptual: `DATA_MODEL.md`.
- Experiencia de usuario: `UX_PRINCIPLES.md`.
- Representación del listado: `PASSENGER_MANIFEST.md`.
- Arquitectura: `ARCHITECTURE.md`.
