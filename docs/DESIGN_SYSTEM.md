# Sistema De Diseño

## Regla General

Ninguna pantalla debe hardcodear colores, tamaños, espaciados, tipografías, radios ni estilos repetidos. Las features consumen tokens semánticos y componentes desde `core/designsystem`.

Los valores crudos pueden existir dentro de la implementación central del sistema de diseño. No deben copiarse en Composables de features.

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

### AppShapes

Radios y formas compartidas para botones, tarjetas, celdas y badges. Las pantallas no crean radios arbitrarios.

## Componentes Previstos

```text
AppButton
AppCard
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

- `AppButton`: acciones visibles, variantes semánticas y objetivo táctil accesible.
- `AppCard`: contenedor consistente para agrupaciones.
- `SeatCell`: celda del mapa con número y estado económico.
- `SeatNumber`: representación prominente y reutilizable del número.
- `StatusBadge`: texto o indicador accesible del estado.
- `FareSelector`: selección entre los tipos habilitados, con `Ida y vuelta` predeterminado.
- `PassengerGroup`: bloque que relaciona un responsable con varios asientos.
- `PaymentButton`: acción explícita para registrar un abono, sin implicar procesamiento bancario.
- `MoneySummary`: total, abonado y pendiente del `Booking`.
- `TourSummary`: fecha, hora y resumen operativo del Tour.

No es necesario crear un componente por cada elemento de una pantalla. Se extrae cuando representa una pieza semántica compartida o evita duplicar estilo y comportamiento.

## Uso Desde Features

- Consumir `AppTheme`, `MaterialTheme` y tokens extendidos; no usar literales `Color(...)` ni HEX.
- Consumir `AppTypography`; no declarar estilos tipográficos repetidos en pantallas.
- Consumir `AppSpacing` y `AppShapes`; no copiar medidas o radios entre features.
- Usar componentes compartidos cuando exista uno adecuado.
- Mantener textos de negocio y contenido accesible separados de los tokens visuales.
- Verificar temas claro y oscuro, contraste, escalado de fuente y objetivos táctiles de al menos `48dp`.

## Estado Actual

El repositorio ya contiene `AppTheme`, esquemas Material 3, tipografía, espaciados, formas, colores de asiento y varios componentes iniciales. Muchos componentes son scaffolding y no constituyen pantallas terminadas. Los tokens semánticos `success`, `warning` y `error`, y algunos componentes previstos, deben consolidarse cuando se implemente UI real; no se deben crear pantallas que los sustituyan con valores locales.
