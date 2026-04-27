# Aplicación Android de Triaje Médico con IA 🏥📱

Aplicación Android desarrollada como parte de un sistema de triaje médico asistido por inteligencia artificial.

---

## 🚀 Descripción del proyecto

**Aplicación Android de Triaje Médico con IA** es la parte móvil de un sistema orientado al ámbito sanitario, diseñado para ayudar a reducir los tiempos de espera en servicios de urgencias.

La aplicación permite que los pacientes se registren e indiquen el motivo de su consulta. Esta información se envía posteriormente al sistema, donde puede ser procesada por un flujo de triaje asistido por inteligencia artificial para clasificar y priorizar a los pacientes según la urgencia de su caso.

Este proyecto nace a partir de un problema real comentado por profesionales sanitarios: la necesidad de revisar rápidamente los pacientes pendientes, sus síntomas y sus motivos de consulta antes de decidir el orden de atención.

---

## 🎯 Problema que busca resolver

En muchos servicios de urgencias, el personal sanitario necesita revisar múltiples pacientes antes de decidir quién debe ser atendido primero.

Cuando hay muchas personas esperando, este proceso puede volverse lento y poco eficiente, ya que el médico o personal responsable debe consultar manualmente los motivos de cada paciente y valorar la prioridad de atención.

Este proyecto propone un sistema digital de apoyo que ayuda a organizar los pacientes por prioridad, ofreciendo una visión más clara, rápida y estructurada de la cola de espera.

---

## 🧩 Contexto del sistema completo

Este repositorio contiene la **aplicación Android orientada al paciente**.

El sistema completo está formado por varias partes:

- Aplicación Android para el registro de pacientes.
- Backend desarrollado con Django.
- Flujo de automatización en n8n para la clasificación mediante IA.
- Panel web para el personal médico.
- Base de datos para almacenar pacientes, consultas y resultados del triaje.

Flujo general del sistema:

```text
Paciente
   ↓
Aplicación Android
   ↓
Backend Django
   ↓
Flujo n8n con IA
   ↓
Clasificación por prioridad
   ↓
Panel web del médico
