# Instrucciones Para Agentes

Gesvi es una aplicación Android offline-first organizada en un único módulo `:app`, con separación lógica Clean + MVVM mediante paquetes `core/` y `feature/`. Antes de editar, carga las skills aplicables de `.agents/skills/`.

## Mapa De Documentación

- Contexto de producto: `docs/PRODUCT.md`.
- Antes de cambiar lógica de negocio: `docs/BUSINESS_RULES.md` y `docs/DATA_MODEL.md`.
- Antes de modificar interfaces, navegación o componentes: `docs/UX_PRINCIPLES.md`, `docs/DESIGN_SYSTEM.md` y `docs/PASSENGER_MANIFEST.md` cuando intervengan listados o impresión.
- Antes de modificar arquitectura o flujo de datos: `docs/ARCHITECTURE.md`.
- Alcance futuro, sin implementación actual: `docs/FUTURE_AI.md`.

## Reglas Permanentes

- Nunca inventar reglas de negocio. Si la documentación no resuelve un caso, pedir confirmación humana.
- Mantener offline-first y conservar el funcionamiento sin conexión.
- Respetar `Presentation -> Domain -> Data`; UI y ViewModels no acceden a DAOs ni a Room entities.
- Todo `PaymentRecord` pertenece a un `Booking`, nunca a un asiento.
- Mapa, listado, impresión y cuentas derivan del mismo estado de repositorio/dominio. No crear fuentes de datos de negocio independientes para cada vista.
- Gesvi solo registra dinero declarado por el encargado. No introducir procesamiento de pagos ni transacciones bancarias.
- No implementar IA, SDKs de IA, backend o sincronización cloud sin un requerimiento explícito.
- Consumir colores, tipografía, formas y espaciados desde `core/designsystem`; no hardcodearlos en pantallas.
- No convertir el proyecto a multi-módulo Gradle sin una decisión explícita.
