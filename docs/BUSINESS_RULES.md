# Reglas De Negocio

Este documento es la fuente principal de verdad para el comportamiento de Gesvi. No se deben completar vacíos con suposiciones. Los casos no definidos se registran como decisiones pendientes y requieren confirmación humana antes de implementarse.

## Tour

Un Tour contiene:

- fecha;
- hora;
- cantidad de asientos;
- valor del flete del carro;
- tipos de pasaje disponibles y el precio de cada uno.

No existe ruta, origen ni destino.

Gesvi tendrá un módulo de Tours registrados desde el que posteriormente podrán editarse fecha, hora, cantidad de asientos y flete.

- La cantidad de asientos puede aumentarse.
- Solo puede reducirse si todos los asientos que desaparecerían están `EMPTY`.

El flete pertenece al Tour. Se usa en los cálculos aprobados del módulo de cuentas y no se muestra en la pantalla operativa de asientos.

## Tipos De Pasaje

Solo existen estos tipos:

```text
Ida y vuelta
Solo ida
Solo venida
```

Reglas:

- `Ida y vuelta` existe siempre.
- `Ida y vuelta` es el tipo predeterminado.
- `Solo ida` es opcional para cada Tour.
- `Solo venida` es opcional para cada Tour.
- No existen tipos personalizados.
- Cada Tour define el precio de sus tipos habilitados. Los precios nunca se hardcodean.

## Asientos

Los asientos de un Tour se numeran según la cantidad configurada. Cada asiento tiene conceptualmente:

- número;
- estado;
- tipo de pasaje cuando está ocupado;
- precio vigente del tipo de pasaje correspondiente.

Una misma reserva puede mezclar tipos y precios:

```text
Asiento 01 -> Ida y vuelta -> $20
Asiento 02 -> Ida y vuelta -> $20
Asiento 03 -> Solo ida     -> $12
Asiento 04 -> Solo ida     -> $12
```

El precio total de la reserva es la suma de los precios vigentes de los tipos elegidos en sus asientos. El dinero no se reparte posteriormente entre esos asientos.

## Cambio De Precios

Cuando cambia el precio de un tipo de pasaje, todos los `BookingSeat` de ese Tour que usan el tipo adoptan automáticamente el nuevo valor. El precio anterior no queda fijado en la reserva.

Los `PaymentRecord` ya registrados no cambian. Se recalculan automáticamente:

- total del `Booking`;
- saldo pendiente;
- estado económico;
- cuentas del Tour.

## Reserva O Venta

El agregado de dominio `Booking` representa una reserva o venta y contiene de `1..N` asientos. Todos comparten un responsable.

```text
Reserva #24
Responsable: Sra. López

Asientos:
01
02
03
04
```

El responsable puede representar a varias personas. No se debe exigir el nombre individual de cada pasajero. La distinción de ciclo de vida entre los términos "reserva" y "venta" todavía no está definida; no se deben inventar estados adicionales por esa diferencia.

## Abonos

Cada abono es un `PaymentRecord` que pertenece al `Booking` completo:

```text
PaymentRecord -> Booking
```

Nunca pertenece a un asiento:

```text
PaymentRecord -X-> Seat
```

Ejemplo:

```text
Sra. López

Asientos: 01 02 03 04

Total:     $64
Abonado:   $30
Pendiente: $34
```

No se puede inferir que el asiento 01 pagó $20 y el 02 pagó $10. Solo se sabe que la responsable entregó $30 para toda la reserva.

Una reserva puede conservar múltiples registros:

```text
$20
$10
$15
```

El historial de registros debe conservarse. Gesvi registra el importe que el encargado declara haber recibido; no cobra, transfiere ni valida dinero mediante servicios bancarios.

Valores derivados:

```text
Total de la reserva = suma del precio vigente de cada BookingSeat.fareType
Total abonado       = suma de PaymentRecord.amount del Booking
Saldo pendiente     = diferencia positiva entre total y total abonado
```

Si lo abonado iguala o supera el total, ya no existe saldo pendiente. La política para el excedente de un sobrepago, devoluciones, correcciones y reversos todavía requiere confirmación. Hasta definirla, no se debe inventar una distribución por asiento ni eliminar historial.

## Estados De Asiento

Los únicos estados económicos del asiento son:

```text
EMPTY
RESERVED
PARTIAL
PAID
```

Representación semántica:

```text
Blanco       EMPTY     Asiento vacío.
Rojo         RESERVED  Reserva sin ningún abono registrado.
Amarillo     PARTIAL   Existe al menos un abono y queda saldo pendiente.
Verde        PAID      No queda saldo pendiente.
```

Derivación:

- `EMPTY`: el asiento no pertenece a ninguna reserva.
- `RESERVED`: pertenece a una reserva y no existe ningún `PaymentRecord` registrado.
- `PARTIAL`: existe al menos un `PaymentRecord` y el total abonado es menor que el total de la reserva.
- `PAID`: existe al menos un `PaymentRecord` y el total abonado iguala o supera el total de la reserva.

Estas reglas presuponen importes y precios válidos. Aún debe confirmarse si se aceptan valores cero o negativos; no se debe implementar su tratamiento por suposición.

El estado se calcula automáticamente. Cuando una reserva contiene varios asientos, todos muestran el mismo estado económico derivado del `Booking`. No se distribuye dinero automáticamente para asignar estados diferentes a cada asiento.

La selección temporal de una celda u otras condiciones de interacción son estado de presentación, no estados económicos adicionales.

## Eliminar Un Asiento De Un Booking

Un `BookingSeat` puede eliminarse individualmente, incluso dentro de una reserva grupal, siempre que el `Booking` conserve al menos otro asiento. No se permite eliminar su último asiento.

Al eliminarlo:

- se elimina únicamente esa asignación;
- el asiento vuelve a `EMPTY`;
- los demás asientos permanecen en el `Booking`;
- los `PaymentRecord` permanecen sin cambios;
- el total se recalcula con los asientos restantes y sus tarifas vigentes;
- el saldo y el estado económico se recalculan automáticamente.

Los abonos existentes no se distribuyen ni se modifican artificialmente.

## Cancelación

La cancelación se refiere a liberar el `Booking` completo y es distinta de eliminar un `BookingSeat` individual.

Una reserva `RESERVED`, sin abonos, puede liberar sus asientos.

Si existe dinero registrado y el estado es `PARTIAL` o `PAID`, no se permite la cancelación simple. El caso debe tratarse posteriormente mediante una operación específica que preserve el historial y evite pérdida de información. Esa operación todavía no está definida.

## Cambio De Asiento

Una asignación solo puede cambiarse a un asiento `EMPTY`. No puede moverse directamente a un asiento `RESERVED`, `PARTIAL` o `PAID`.

El cambio conserva:

- la reserva;
- el responsable;
- el tipo de pasaje;
- los abonos de la reserva;
- el saldo derivado;
- el estado económico derivado.

El asiento anterior pasa a `EMPTY`. El valor aplicable continúa siendo el precio vigente en el Tour para el tipo de pasaje conservado.

## Cuentas Del Tour

- El total abonado del Tour se obtiene de los `PaymentRecord` de sus reservas.
- El saldo pendiente del Tour se obtiene sumando los saldos de sus reservas, sin redistribuir dinero entre asientos ni entre reservas.
- Totales, saldos y estados usan siempre las tarifas vigentes del Tour.
- El flete del carro se toma del Tour y solo participa en cálculos de cuentas aprobados.
- No se debe inventar una fórmula de ganancia, utilidad o cierre distinta de las reglas confirmadas.

## Fuente Única De Verdad

La ocupación, tipos asignados, abonos y estados económicos provienen del estado de `BookingRepository` y del dominio. Las tarifas vigentes, la configuración y el flete provienen del Tour. Los casos de uso combinan esas fuentes para producir una misma proyección coherente.

```text
Repository / Domain state
          |
          +-- Seat Map
          +-- Passenger List
          +-- Passenger PDF
          +-- Accounts
```

No se permiten tablas, repositorios o estados de negocio independientes como `SeatMapData`, `PassengerListData` o `PrintData`. Puede existir estado transitorio de UI, como selección o texto en edición, pero nunca otra copia autoritativa de reservas, asientos, pagos o saldos.

## Decisiones Pendientes

Requieren confirmación humana antes de implementarse:

- ciclo de vida formal y diferencia, si existe, entre reserva y venta;
- moneda, precisión, redondeo y formato monetario;
- validez de precios y abonos con importe cero o negativo;
- tratamiento de sobrepagos;
- operación de devolución, corrección o reverso de abonos;
- operación de cancelación cuando existe dinero;
- fórmulas adicionales de utilidad o cierre de cuentas.
