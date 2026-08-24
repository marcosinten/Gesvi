---
name: mobile-ux
description: Reglas de experiencia de usuario específicas para esta aplicación.
---

# Mobile UX Skill

Al diseñar o modificar pantallas para esta aplicación, debes adherirte estrictamente a estos principios:

1. **Mobile-first y accesible:** Los botones deben ser grandes (mínimo 48dp).
2. **Baja carga cognitiva:** Evitar pantallas llenas de información. Mostrar solo lo necesario.
3. **Gestos evidentes:** Todo elemento interactivo debe parecer interactivo.
4. **Estados visuales:** Usa `SeatStatus` del `AppTheme` para colorear los asientos (Empty, Reserved, Partial, Paid). NUNCA inventes colores nuevos en las pantallas.

Si el usuario solicita una UI compleja, simplifícala proponiendo una alternativa más fácil de usar para personas mayores.
