# Arquitectura

## Alcance Actual

Gesvi se mantiene como un único módulo Gradle `:app`. La separación es lógica, mediante paquetes `core/` y `feature/`; esta etapa no requiere una migración a multi-módulo.

Tecnologías conservadas:

- Kotlin;
- Jetpack Compose y Material 3;
- MVVM;
- StateFlow y Coroutines;
- Room;
- Hilt;
- Navigation Compose;
- Gradle Kotlin DSL.

## Capas

```text
Presentation
     |
     v
Domain
     |
     v
Data
```

### Presentation

Puede conocer:

- Composables;
- ViewModels;
- `StateFlow`;
- UI State;
- Events;
- casos de uso;
- modelos de dominio o modelos de presentación derivados.

No puede conocer directamente:

- DAOs;
- Room entities o relaciones Room;
- `AppDatabase`;
- SQL;
- implementaciones concretas de repositorios.

Los Composables emiten eventos y renderizan estado. El ViewModel coordina casos de uso y expone estado inmutable. Los Composables profundos reciben datos y lambdas, no un `NavController`.

### Domain

Contiene:

- modelos de dominio;
- interfaces de repositorio;
- casos de uso;
- reglas e invariantes del negocio.

Domain no depende de Android, Compose, Material, Room, DAOs ni entities. Los estados de interacción visual, como una celda seleccionada, pertenecen a Presentation.

### Data

Contiene:

- Room y fuentes de datos locales;
- DAOs;
- entities y relaciones de persistencia;
- mappers;
- implementaciones de repositorios.

Data implementa contratos de Domain. Una entity no sale de esta capa. Las operaciones que modifican un agregado completo, como crear un `Booking` con sus asientos, deben respetar un único límite transaccional.

## Flujo De Una Feature

```text
Compose
   |
   v
ViewModel
   |
   v
Use Case
   |
   v
Repository interface
   |
   v
Repository implementation
   |
   v
Local data source / Room DAO
```

Dependencias prohibidas:

```text
Composable -> DAO
ViewModel  -> DAO
Composable -> Room Entity
ViewModel  -> Room Entity
Domain     -> Room
Domain     -> Design System
```

## Repository Pattern Y Offline-First

Room es la fuente local de persistencia. Los repositorios exponen modelos de dominio y flujos reactivos; la UI no necesita saber dónde se almacenan.

La arquitectura puede evolucionar sin cambiar Presentation:

```text
Repository
 |-- LocalDataSource
 `-- RemoteDataSource (futuro y opcional)
```

No existe `RemoteDataSource`, backend ni sincronización en el alcance actual. Si se añadieran, el trabajo diario debe seguir disponible offline y habría que definir explícitamente resolución de conflictos y autoridad de datos.

## Fuente Única De Verdad

`BookingRepository` es la fuente autoritativa de reservas, asignaciones y abonos. La configuración del viaje procede de su repositorio. Los casos de uso derivan una proyección coherente para todos los consumidores:

```text
Repository / Domain state
          |
          +-- Seat Map
          +-- Passenger List
          +-- Printable Manifest
          `-- Accounts
```

Todas estas proyecciones resuelven la ocupación de un número de asiento
usando el mismo tramo activo del Tour (`Trip.currentLeg()`, ver
`docs/DATA_MODEL.md`). Ese cálculo se hace en el momento de leer el estado,
nunca mediante un job en segundo plano ni una bandera persistida; ninguna
proyección debe implementar su propio criterio de qué tramo está activo.

No se permiten fuentes persistentes independientes como:

```text
SeatMapData
PassengerListData
PrintData
AccountsData
```

Sí se permite estado estrictamente transitorio de Presentation, como asiento seleccionado, diálogo abierto o texto de formulario. Ese estado no puede duplicar ni contradecir reservas, pagos, saldos u ocupación.

## Organización Actual

```text
app/src/main/java/com/gestionviajes/
|-- core/
|   |-- common/
|   |-- database/
|   |-- designsystem/
|   |-- domain/
|   `-- navigation/
`-- feature/
    |-- accounts/
    |-- bookings/
    |-- collections/
    |-- passenger_list/
    |-- seats/
    |-- settings/
    `-- trips/
```

Cada feature puede organizarse internamente en `presentation/`, `domain/` y `data/` según lo necesite. No se deben crear carpetas vacías ni abstracciones sin uso solo para completar una plantilla.

`AppDatabase` registra centralmente las entities y `AppNavGraph` registra las pantallas. En el módulo único funcionan como puntos de composición que conocen clases concretas de features; esto no autoriza a saltarse capas dentro del flujo funcional.

## Estado Del Scaffolding

- `trips` es la única feature con un flujo MVVM/Data parcialmente conectado.
- La configuración de tarifas del Tour conserva el precio almacenado al desactivar un tipo. Las banderas de disponibilidad solo controlan nuevas asignaciones y no constituyen una fuente de datos de reservas.
- `bookings` expone reservas, asignaciones y abonos desde Room mediante un repositorio reactivo, crea reservas y ventas dentro de una única transacción y añade cada nuevo abono como un `PaymentRecord` independiente.
- El Detalle del Tour genera los asientos `1..seatCount` y combina la configuración del Tour con `BookingRepository`. Permite seleccionar vacíos, proyecta cada Booking ocupado como un grupo con estado económico, tarifas y saldos derivados, y registra abonos sin duplicar estado de negocio en Presentation. La ocupación por número de asiento se filtra por el tramo activo del Tour (`Trip.currentLeg()`): un mismo asiento puede tener asignaciones distintas en IDA y en VENIDA, y solo la del tramo activo se proyecta como ocupación visible.
- `booking_seat_cross_ref` no tiene una columna de tramo propia; el tramo que ocupa cada asignación se deriva de su `FareType` (`Solo ida` → IDA, `Solo venida` → VENIDA, `Ida y vuelta` → ambos). Las altas transaccionales validan que un asiento entrante no se superponga en tramo con una asignación ya existente, en vez de exigir que el asiento esté completamente libre.
- listado, cuentas e impresión todavía no están implementados funcionalmente.
- Las pantallas y componentes existentes son scaffolding, no funcionalidades completas.

Las diferencias entre el esquema provisional y el modelo objetivo están documentadas en `DATA_MODEL.md`. Deben resolverse como una migración coherente, no mediante estados paralelos o accesos directos a Room.

## Inyección Y Pruebas

Hilt enlaza interfaces con implementaciones en módulos de composición. Un constructor `@Inject` no sustituye el binding cuando el consumidor solicita una interfaz.

Como mínimo, la implementación futura debe cubrir con pruebas:

- derivación de los cuatro estados económicos;
- múltiples asientos y tarifas en un mismo `Booking`;
- pertenencia de `PaymentRecord` a `Booking`;
- cambio solo hacia `EMPTY`;
- creación transaccional y prevención de doble asignación en el mismo tramo (IDA/VENIDA), permitiendo asignaciones distintas por tramo en el mismo asiento;
- proyecciones equivalentes para mapa, listado, impresión y cuentas;
- migraciones Room sin pérdida de datos.
