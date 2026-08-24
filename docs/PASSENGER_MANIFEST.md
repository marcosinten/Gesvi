# Listado De Pasajeros

## Propósito

El listado de pasajeros conserva la representación tradicional que el encargado reconoce en papel. No es una libreta digital separada ni una segunda fuente de datos: es otra vista del mismo estado de reservas y asientos que utiliza el mapa.

## Numeración

El usuario debe poder recorrer todos los números configurados para el Tour:

```text
01
02
03
04
05
...
N
```

Los números de asiento deben ser prominentes y conservar un orden estable. Los asientos vacíos también deben poder identificarse como tales.

## Tramo Activo

Un mismo número de asiento puede tener ocupantes distintos en IDA y en
VENIDA. El listado de pasajeros muestra, para cada número de asiento, la
ocupación del tramo activo del Tour (ver docs/BUSINESS_RULES.md y
docs/DATA_MODEL.md): antes de la hora de venida se ve quién ocupa la IDA;
desde la hora de venida en adelante se ve quién ocupa la VENIDA, y si no hay
ocupación de VENIDA para ese asiento, se muestra vacío aunque haya tenido
pasajero en IDA. El listado no calcula su propio tramo activo: reutiliza el
mismo valor derivado que usan el mapa de asientos, el PDF y las cuentas.

## Agrupación Por Reserva

Cuando varios asientos pertenecen a una misma reserva, la relación debe ser evidente:

```text
01 ┐
02 │
03 │  Sra. López
04 ┘
```

En móvil puede usarse una tarjeta o bloque agrupado en lugar del corchete literal, siempre que no se pierda la correspondencia entre responsable y asientos.

Ejemplo de bloque:

```text
Sra. López

[01] Ida y vuelta $20.00
[02] Ida y vuelta $20.00
[03] Solo ida     $12.00
[04] Solo ida     $12.00

Total:     $64.00
Abonado:   $30.00
Pendiente: $34.00

[Registrar abono]
```

El nombre mostrado es el responsable de la reserva. No se debe inventar ni exigir un nombre individual para cada asiento.

## Dinero Y Estado

- El total se obtiene usando el precio vigente en el Tour para el tipo de cada `BookingSeat`.
- El abonado se obtiene sumando los `PaymentRecord` del `Booking`.
- El pendiente y el estado se derivan de esa misma reserva.
- Todos los asientos agrupados comparten el estado económico.
- El listado no distribuye el total abonado entre asientos.
- El total, el abonado y el pendiente de una reserva incluyen todos sus
  `BookingSeat`, sin importar si ocupan IDA, VENIDA o ambos: el tramo activo
  decide qué se muestra como ocupación del número de asiento, no qué se
  cuenta para el dinero de la reserva.

## Fuente Única De Verdad

```text
BookingRepository / Domain state
                 |
                 +-- Seat Map
                 +-- Passenger List
                 +-- Passenger PDF
```

Mapa, listado y PDF deben reaccionar a la misma información. Está prohibido persistir una copia propia del listado o preparar datos del PDF que se conviertan en otra fuente autoritativa.

Una proyección de UI o un modelo de PDF puede adaptar el formato, pero debe generarse desde el estado actual del repositorio/dominio y ser descartable.

## Versión En PDF

El listado de pasajeros debe poder generarse en PDF. El documento conserva una apariencia cercana al formato físico tradicional y muestra, como mínimo:

- número de asiento;
- responsable;
- tipo de pasaje;
- valor individual;
- estado;
- agrupación por reserva.

El orden de los asientos debe ser determinista y comprensible. El PDF se genera desde el estado actual del repositorio/dominio y no autoriza una base de datos, caché o flujo de negocio separado.
