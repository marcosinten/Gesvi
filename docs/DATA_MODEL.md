# Modelo De Datos

## Alcance

Este documento describe el modelo conceptual del negocio. No prescribe una pantalla ni exige que cada concepto sea una tabla independiente. El modelo de dominio, las entities de Room y los mappers deben mantenerse separados.

```text
Domain model <-> Mapper <-> Room entity / relation
```

Una Room entity no debe llegar a Presentation. Un modelo de dominio no debe depender de Room, Android ni Compose.

## Entidades Conceptuales

### Tour

Representa un Tour y contiene:

- identificador;
- fecha;
- hora;
- cantidad de asientos;
- valor del flete del carro;
- configuraciones de tipos de pasaje.

No contiene ruta, origen ni destino. El scaffolding actual todavía usa el nombre técnico `Trip`; ese modelo es provisional y representa el mismo concepto de negocio.

La cantidad de asientos puede aumentar. Para reducirla, los números que queden fuera de la nueva cantidad deben estar `EMPTY`, es decir, sin ningún `BookingSeat` asociado.

### FareType

Representa uno de los tres tipos permitidos dentro de un Tour:

```text
Ida y vuelta
Solo ida
Solo venida
```

Contiene el tipo canónico, si está habilitado para el Tour y su precio vigente. `Ida y vuelta` siempre existe y es el predeterminado. No se admiten tipos personalizados ni precios hardcodeados.

La forma exacta de persistir la configuración puede ser una relación o entity propia, pero no debe convertir etiquetas libres de UI en tipos de negocio.

### Seat

Representa conceptualmente una posición numerada dentro del Tour. El conjunto de asientos se obtiene de `Tour.seatCount` y su ocupación se deriva de las asignaciones de reservas.

Por esta razón, `Seat` no necesita necesariamente una tabla con estado mutable. En particular, `EMPTY`, `RESERVED`, `PARTIAL` y `PAID` no deben almacenarse como una fuente independiente: se calculan desde la existencia de una asignación y el estado económico de su `Booking`.

### Booking

Es el agregado que representa una reserva o venta. Contiene:

- identificador;
- Tour al que pertenece;
- nombre del responsable;
- asignaciones activas `BookingSeat`;
- cero o más `PaymentRecord`.

Se crea con una o más asignaciones, pero puede quedar sin asientos activos después de liberarlos. Si tiene historial, el `Booking` se conserva sin asientos activos o con una representación equivalente a cancelado/cerrado. No requiere nombres individuales por pasajero.

Total, recibido, saldo pendiente, saldo a favor y estado económico son valores derivados del agregado.

### BookingSeat

Representa la asignación de un asiento a una reserva. Contiene conceptualmente:

- referencia al `Booking`;
- número de asiento;
- `FareType` elegido.

El modelo y la persistencia deben garantizar que un mismo número de asiento no esté asignado a dos reservas dentro del mismo Tour. Crear una reserva y sus asignaciones debe ser una operación atómica.

El precio de un `BookingSeat` se deriva del precio vigente de su `FareType` en el Tour. No es una tarifa histórica fijada por la reserva. Cambiar el precio actualiza automáticamente los valores derivados sin modificar los `PaymentRecord`.

Puede eliminarse cualquier `BookingSeat`, incluido el último activo. Su asiento vuelve a `EMPTY`; el `Booking` y sus abonos históricos permanecen.

### PaymentRecord

Representa un abono declarado por el encargado. Contiene conceptualmente:

- identificador;
- referencia al `Booking`;
- importe;
- momento de registro necesario para conservar el historial.

Los `PaymentRecord` no se reescriben automáticamente cuando cambian tarifas, tipos de pasaje o asientos.

La relación obligatoria es:

```text
PaymentRecord -> Booking
```

La siguiente relación está prohibida:

```text
PaymentRecord -> Seat
```

## Relaciones

```text
Tour
 |-- FareTypes
 |-- Seats (derivados de seatCount)
 `-- Bookings

Booking
 |-- ResponsibleName
 |-- BookingSeats
 `-- PaymentRecords

BookingSeat
 |-- Seat
 |-- FareType
 `-- Current FareType price (derivado)

PaymentRecord
 `-- Booking
```

Cardinalidades e invariantes:

- Un `Tour` tiene la configuración de sus asientos y tarifas.
- Un `Booking` pertenece a un solo `Tour`.
- Un `Booking` se crea con `1..N` `BookingSeat` activos y puede quedar con `0..N` después de liberaciones.
- Un `BookingSeat` pertenece a un solo `Booking`.
- Un asiento puede pertenecer como máximo a un `Booking` dentro del mismo Tour.
- Un `PaymentRecord` pertenece a un solo `Booking`.
- Un `Booking` puede tener de `0..N` `PaymentRecord`.

## Valores Derivados

```text
bookingTotal   = sum(precio vigente de cada BookingSeat activo)
totalRecibido  = sum(PaymentRecord.amount)
saldoPendiente = max(bookingTotal - totalRecibido, 0)
saldoFavor     = max(totalRecibido - bookingTotal, 0)
```

El estado económico también considera si existe algún `PaymentRecord`: sin registros es `RESERVED`; con registros es `PARTIAL` mientras exista saldo pendiente y `PAID` cuando deje de existir, incluso si hay saldo a favor. Se deriva para el `Booking` y se proyecta igual sobre todos sus asientos activos.

La validez de importes cero o negativos y la forma de registrar una futura devolución requieren confirmación humana. Gesvi conserva el saldo a favor, pero no procesa la devolución.

## Fuente Única De Verdad

`BookingRepository` debe exponer una proyección reactiva capaz de reconstruir reservas, asignaciones y abonos. Los casos de uso combinan esa información con la configuración y las tarifas vigentes del Tour.

```text
Room relations -> Mappers -> Repository -> Domain state
                                      |
                                      +-- Seat Map
                                      +-- Passenger List
                                      +-- Passenger PDF
                                      `-- Accounts
```

No crear entities o tablas autoritativas para `SeatMap`, `PassengerList`, PDF o `Accounts`. Sus modelos de presentación son proyecciones descartables del mismo estado de dominio.

## Room Y Mappers

- Las Room entities viven en Data y reflejan decisiones de persistencia, no necesidades visuales.
- Los DAOs no salen de Data.
- Los repositorios implementan interfaces de Domain y coordinan relaciones y transacciones.
- Los mappers convierten explícitamente entities/relations a modelos de dominio y viceversa.
- Las foreign keys e índices deben proteger pertenencia e integridad referencial.
- Todo cambio de esquema requiere incrementar versión, exportar el schema y proporcionar una migración antes de producción.
- Una aplicación offline-first no debe depender de `fallbackToDestructiveMigration()` para datos reales.

## Estado Del Scaffolding Actual

El esquema actual es provisional y no implementa todavía este modelo completo:

- `Trip` aún conserva origen, destino y una tarifa predeterminada, pero el concepto objetivo es `Tour`, sin ruta y con hora, cantidad de asientos, flete y tarifas por tipo.
- `BookingSeatCrossRef` aún carece de tipo de pasaje y protección completa contra doble asignación por Tour.
- `BookingRepository` no tiene implementación ni reconstruye asignaciones y abonos.
- El dominio de `Booking` usa un estado visual por asiento en lugar de derivarlo del agregado.
- Los importes actuales usan `Double`; moneda, precisión y redondeo siguen pendientes.

Corregir estos puntos exige definir una migración coherente y forma parte de la futura implementación funcional. No deben resolverse con cambios aislados que puedan perder datos.
