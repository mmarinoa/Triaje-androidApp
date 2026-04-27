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

---

## 🔄 Flujo general del sistema

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

---

## ✨ Funcionalidades principales

- Registro de pacientes.
- Pantalla de inicio de sesión.
- Validación de datos introducidos por el usuario.
- Formulario para indicar el motivo de la consulta.
- Envío de información al backend.
- Diseño orientado a un flujo de urgencias médicas.
- Interfaz pensada para introducir la información de forma rápida.
- Integración prevista con sistema de triaje mediante IA.

---

## 🧠 Triaje asistido por inteligencia artificial

La aplicación recoge la información del paciente y el motivo de la consulta.

Posteriormente, estos datos se envían al backend, donde pueden ser procesados por un flujo de automatización conectado a un modelo de inteligencia artificial. Este flujo analiza la información recibida y clasifica el caso según criterios de prioridad.

El objetivo no es sustituir al personal sanitario, sino ofrecer una herramienta de apoyo que ayude a ordenar la información y agilizar la toma de decisiones.

---

## 🛠️ Tecnologías utilizadas

- **Java**
- **Android Studio**
- **Gradle**
- **XML Layouts**
- **Integración con APIs REST**
- **Django**
- **n8n**
- **Inteligencia Artificial**
- **Base de datos SQL**

---

## 📁 Estructura del proyecto

Triaje-androidApp/  
├── app/  
│   └── src/  
│       └── main/  
│           ├── java/  
│           ├── res/  
│           └── AndroidManifest.xml  
├── gradle/  
├── .gitignore  
├── build.gradle  
├── gradle.properties  
├── gradlew  
├── gradlew.bat  
├── settings.gradle  
└── README.md  

---

## 📱 Pantallas principales

La aplicación incluye pantallas centradas en la interacción del paciente con el sistema.

Pantallas principales:

- Pantalla de login.
- Pantalla de registro.
- Pantalla principal.
- Formulario de motivo de consulta.
- Validaciones de campos.
- Flujo de envío de datos al sistema.

---

## 🔐 Datos y privacidad

Este proyecto está desarrollado como prototipo académico y técnico.

No se deben almacenar datos reales de pacientes en este repositorio.

La información sensible, como URLs privadas, claves de API, tokens, credenciales o datos médicos, debe mantenerse siempre fuera del código fuente público.

Para ello, se recomienda utilizar:

- `local.properties`
- variables de entorno
- archivos de configuración ignorados por Git
- `BuildConfig`

No se deben subir a GitHub:

- claves API
- tokens
- credenciales
- URLs privadas
- datos médicos reales
- información personal de pacientes

---

## ⚙️ Instalación y ejecución

### Requisitos

- Android Studio
- JDK compatible
- Gradle
- Android SDK
- Emulador Android o dispositivo físico

### Pasos

Clonar el repositorio:

```bash
git clone https://github.com/mmarinoa/Triaje-androidApp.git
