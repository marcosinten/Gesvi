---
name: mobile-ux
description: Usar al modificar pantallas, navegación, formularios, asientos visuales, listados, impresión, cobros o componentes UI de Gesvi.
---

# Mobile UX Skill

Activa esta skill al modificar:

- pantallas o navegación;
- formularios;
- mapa o representación visual de asientos;
- listado de pasajeros;
- representación imprimible;
- cobros o resúmenes de dinero;
- componentes de `core/designsystem`.

Antes de editar, lee:

1. `docs/UX_PRINCIPLES.md`.
2. `docs/DESIGN_SYSTEM.md`.
3. `docs/PASSENGER_MANIFEST.md` cuando intervengan listado, agrupaciones o impresión.

Reglas obligatorias:

- Diseñar mobile-first para personas adultas con poca familiaridad tecnológica.
- Respetar el proceso físico conocido: listado numerado, cuaderno, nombres y agrupaciones.
- Mantener visibles las acciones principales y usar objetivos táctiles de al menos `48dp`.
- No depender de swipe, long press ni otros gestos ocultos para acciones principales.
- Minimizar escritura mediante selectores y opciones predefinidas.
- Hacer muy visible el número de asiento y ofrecer feedback inmediato.
- No depender solo del color para comunicar estado.
- Consumir colores, tipografías, formas y espaciados del design system; no crear valores locales.
- Mantener el mismo significado de estado en Asientos, Lista, Cobros y Cuentas.

Si una propuesta complejiza el flujo sin una necesidad del negocio, simplifícala sin ocultar información crítica.
