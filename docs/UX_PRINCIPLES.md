# Principios UX

## Modelo Mental Del Usuario

Los usuarios principales son personas adultas con poca familiaridad tecnológica. Están acostumbrados a trabajar físicamente con:

- un listado numerado;
- un cuaderno;
- números de asiento;
- nombres escritos;
- agrupaciones de varios asientos mediante corchetes.

Gesvi debe digitalizar ese proceso, no reemplazarlo por un modelo abstracto o radicalmente distinto. El listado agrupado se especifica en `PASSENGER_MANIFEST.md`.

## Principios Obligatorios

- **Mobile-first y Android:** Diseñar primero para teléfonos Android y uso táctil.
- **Pocos pasos:** La tarea principal debe completarse con el mínimo de decisiones y pantallas razonable.
- **Botones grandes:** Los objetivos táctiles deben ser claros y tener al menos `48dp`.
- **Texto legible:** Usar la jerarquía de `AppTypography` y evitar texto pequeño para información crítica.
- **Acciones visibles:** Las acciones principales deben mostrarse como botones o controles reconocibles.
- **Sin gestos ocultos:** Un gesto puede ser complementario, nunca la única forma de ejecutar una acción importante.
- **Sin long press principal:** No usar pulsación prolongada como acceso primario a reservar, cobrar, mover o cancelar.
- **Escritura mínima:** Preferir selectores, opciones predefinidas y teclado numérico cuando corresponda.
- **Sin configuración innecesaria:** Mostrar únicamente opciones que tengan efecto en la tarea actual.
- **Asientos prominentes:** El número de asiento debe ser uno de los elementos más visibles del mapa y del listado.
- **Lenguaje sencillo:** Usar palabras conocidas por el encargado y evitar jerga técnica o bancaria.
- **Feedback inmediato:** Confirmar visualmente selección, guardado, error y cambio de estado.
- **No depender solo del color:** Acompañar estados con texto, etiqueta, icono o descripción accesible.
- **Recuperación segura:** Antes de acciones destructivas, explicar la consecuencia y ofrecer una confirmación visible cuando corresponda.

## Áreas Principales

La navegación principal mantiene siempre este orden:

```text
Informes | Inicio | Historial
```

- `Informes` concentra el acceso al listado para imprimir o guardar en PDF y a futuros reportes, todos derivados del mismo estado de dominio.
- `Inicio` es el destino inicial y muestra el resumen y acceso a los Tours registrados.
- `Historial` queda como destino visible, pero su comportamiento funcional no se define ni implementa todavía.

Dentro de un Tour, el usuario debe poder trabajar principalmente con:

```text
Asientos
Lista
Cobros
Cuentas
```

Estas áreas son distintas representaciones o tareas sobre la misma información del viaje. Cambiar una reserva o registrar un abono debe reflejarse inmediatamente en las demás, sin sincronizaciones manuales.

En Detalle del Tour, la cabecera completa forma parte del desplazamiento: se oculta al avanzar por el mapa y reaparece únicamente al regresar al inicio. Este comportamiento no modifica las cabeceras de las demás pantallas.

## Formularios

- Solicitar solo datos definidos por las reglas del negocio.
- No solicitar origen, ruta detallada ni nombres individuales cuando no son necesarios.
- Mostrar valores predeterminados seguros, como `Ida y vuelta`, sin ocultar las alternativas habilitadas.
- Conservar lo escrito ante errores validables.
- Mostrar errores junto al campo o acción que los produjo y explicar cómo corregirlos.

## Consistencia Visual

Las pantallas consumen tokens y componentes de `DESIGN_SYSTEM.md`. No deben hardcodear colores, tamaños, espaciados, tipografías ni formas. Los estados `EMPTY`, `RESERVED`, `PARTIAL` y `PAID` deben mantener el mismo significado en mapa, lista, cobros, cuentas e impresión.
