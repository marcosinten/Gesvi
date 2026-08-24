# Sistema De Diseño

## Regla General

Ninguna pantalla debe hardcodear colores, tamaños, espaciados, tipografías, radios ni estilos repetidos. Las features consumen tokens semánticos y componentes desde `core/designsystem`.

Los valores crudos pueden existir dentro de la implementación central del sistema de diseño. No deben copiarse en Composables de features.

## Identidad Visual

Gesvi usa una estética de ruta amable: fondo azul niebla, índigo como color de marca y superficies blancas muy redondeadas con elevación suave. Verde, amarillo, celeste y rosado funcionan como acentos breves para orientar o ilustrar; no compiten con las acciones principales ni sustituyen etiquetas.

La firma visual es una línea de recorrido con paradas de color dentro de las cabeceras curvas. Es decorativa y nunca transporta información necesaria. Los docks inferiores también usan una silueta curva y conservan icono y texto visibles para evitar navegación ambigua.

## Fundamentos

### AppTheme

Punto de entrada del tema. Envuelve la aplicación y publica colores, tipografía, espaciados y formas para temas claro y oscuro.

### AppColors

Contrato conceptual de colores semánticos. La implementación actual combina `MaterialTheme.colorScheme` con `AppExtendedColors`; no es obligatorio renombrarla si conserva el mismo contrato y un acceso centralizado.

Tokens mínimos:

```text
primary
background
surface
textPrimary
textSecondary

seatEmpty
seatReserved
seatPartial
seatPaid

success
warning
error

accentGreen
accentYellow
accentSky
accentPink
brandSurface
onBrandSurface
```

Significado de asientos:

- `seatEmpty`: blanco.
- `seatReserved`: rojo.
- `seatPartial`: amarillo.
- `seatPaid`: verde.

El color comunica semántica, pero nunca debe ser el único indicador de estado.

### AppTypography

Escala tipográfica común basada en Material 3. Los números de asiento, importes y acciones principales deben usar estilos legibles de esta escala, sin tamaños `sp` locales.

### AppSpacing

Escala común para padding, separación, tamaño mínimo y distribución. Las features no introducen valores `dp` repetidos cuando existe un token aplicable.

### AppDimensions

Dimensiones semánticas para objetivos táctiles, botones, iconos, celdas y elevaciones. El objetivo táctil mínimo continúa siendo `48dp`; las acciones principales usan una altura mayor para facilitar su reconocimiento.

### AppShapes

Radios y formas compartidas para botones, tarjetas, celdas y badges. Las pantallas no crean radios arbitrarios.

## Componentes Previstos

```text
AppButton
AppCard
AppHeroHeader
AppScreenScaffold
AppBottomNavigation
AppIconBadge
AppEmptyState
AppPlaceholderScreen
SeatCell
SeatNumber
StatusBadge
FareSelector
PassengerGroup
PaymentButton
MoneySummary
TourSummary
```

Responsabilidades:

- `AppButton`: acciones visibles, variantes semánticas y tamaños estándar o compacto, siempre con objetivo táctil accesible.
- `AppCard`: contenedor consistente para agrupaciones.
- `AppHeroHeader`: cabecera índigo curva con título, contexto, navegación opcional e ilustración.
- `AppScreenScaffold`: estructura de pantalla que aplica cabecera, fondo e insets de forma consistente.
- `AppBottomNavigation`: dock curvo con etiquetas siempre visibles y áreas táctiles amplias.
- `AppIconBadge`: icono ilustrativo sobre un acento semántico.
- `AppEmptyState`: explicación, ilustración y acción opcional para estados sin contenido.
- `AppPlaceholderScreen`: composición temporal consistente para destinos aún no implementados.
- `SeatCell`: silla táctil con apoyos laterales, número prominente, estado económico y selección transitoria diferenciada.
- `SeatNumber`: representación prominente y reutilizable del número.
- `StatusBadge`: texto o indicador accesible del estado.
- `FareSelector`: selección entre los tipos habilitados, con `Ida y vuelta` predeterminado.
- `PassengerGroup`: bloque que relaciona un responsable con varios asientos.
- `PaymentButton`: acción explícita para registrar un abono, sin implicar procesamiento bancario.
- `MoneySummary`: total, abonado y pendiente del `Booking`.
- `TourSummary`: fecha, hora y resumen operativo del Tour.

No es necesario crear un componente por cada elemento de una pantalla. Se extrae cuando representa una pieza semántica compartida o evita duplicar estilo y comportamiento.

### Distribución Del Bus

- Las filas normales muestran `2 asientos | pasillo | 2 asientos`.
- Los últimos cinco números forman una fila trasera continua, sin pasillo central.
- Cuando la cantidad anterior a la fila trasera no completa cuatro posiciones, la fila parcial se llena de izquierda a derecha y conserva los huecos físicos restantes.
- La distribución es una proyección de Presentation sobre los asientos `1..seatCount`; no crea ni persiste asientos adicionales.
- El número es prominente, cada celda conserva al menos `48dp` de área táctil y la selección usa un borde primario además del estado económico.
- Si la escala de fuente o la cantidad de dígitos requiere más ancho, las celdas crecen y el interior permite desplazamiento horizontal antes de recortar información.
- El mapa presenta una cabina y un pasillo continuo; la forma de silla y el ancho de celda se mantienen consistentes entre filas normales y trasera.
- La celda prioriza visualmente el número. La leyenda situada al final del mapa y la descripción accesible comunican el estado sin depender solo del color.
- Al seleccionar un asiento ocupado, todos los asientos de su `Booking` conservan el color económico y reciben el mismo borde primario de selección, aunque estén separados físicamente.

## Uso Desde Features

- Consumir `AppTheme`, `MaterialTheme` y tokens extendidos; no usar literales `Color(...)` ni HEX.
- Consumir `AppTypography`; no declarar estilos tipográficos repetidos en pantallas.
- Consumir `AppSpacing` y `AppShapes`; no copiar medidas o radios entre features.
- Usar componentes compartidos cuando exista uno adecuado.
- Mantener textos de negocio y contenido accesible separados de los tokens visuales.
- Verificar temas claro y oscuro, contraste, escalado de fuente y objetivos táctiles de al menos `48dp`.

## Estado Actual

El repositorio contiene `AppTheme`, esquemas Material 3 claro y oscuro, tipografía legible, espaciados, dimensiones, formas curvas, colores semánticos y componentes de estructura. `PassengerGroup`, `FareSelector` y `MoneySummary` continúan como scaffolding y deberán completarse cuando exista su contrato funcional; no se deben crear pantallas que los sustituyan con valores locales.
