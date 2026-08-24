# Listado De Pasajeros

## Propósito

El listado de pasajeros conserva la representación tradicional que el encargado reconoce en papel. No es una libreta digital separada ni una segunda fuente de datos: es otra vista del mismo estado de reservas y asientos que utiliza el mapa.

## Numeración

El usuario debe poder recorrer todos los números configurados para el viaje:

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

[01] Ida y vuelta $20
[02] Ida y vuelta $20
[03] Solo ida     $12
[04] Solo ida     $12

Total:     $64
Abonado:   $30
Pendiente: $34

[Registrar abono]
```

El nombre mostrado es el responsable de la reserva. No se debe inventar ni exigir un nombre individual para cada asiento.

## Dinero Y Estado

- El total se obtiene sumando los precios de las asignaciones `BookingSeat`.
- El abonado se obtiene sumando los `PaymentRecord` del `Booking`.
- El pendiente y el estado se derivan de esa misma reserva.
- Todos los asientos agrupados comparten el estado económico.
- El listado no distribuye el total abonado entre asientos.

## Fuente Única De Verdad

```text
BookingRepository / Domain state
                 |
                 +-- Seat Map
                 +-- Passenger List
                 +-- Printable Manifest
```

Mapa, listado e impresión deben reaccionar a la misma información. Está prohibido persistir una copia propia del listado o preparar datos de impresión que se conviertan en otra fuente autoritativa.

Una proyección de UI o un modelo de impresión puede adaptar el formato, pero debe generarse desde el estado actual del repositorio/dominio y ser descartable.

## Versión Imprimible

La impresión debe conservar una apariencia cercana al formato físico tradicional y mostrar, como mínimo:

- número de asiento;
- responsable;
- tipo de pasaje;
- valor individual;
- estado;
- agrupación por reserva.

El orden de los asientos debe ser determinista y comprensible. El medio concreto de impresión, el tamaño de papel y el formato técnico todavía requieren confirmación humana. Este documento no autoriza implementar una base de datos, caché o flujo de negocio separado para imprimir.
