# Aplicación Android de Triaje Médico con IA 🏥📱

Aplicación Android desarrollada como parte del proyecto **Sistema de triaje inteligente para urgencias**, realizado para el TFC del ciclo de **Desarrollo de Aplicaciones Multiplataforma**.

Esta aplicación es la parte móvil del sistema y está orientada al paciente. Permite que una persona pueda registrarse, iniciar sesión y, más adelante, enviar el motivo de su consulta al backend desarrollado con Django.

El objetivo final del sistema completo es ayudar a organizar mejor la entrada de pacientes en urgencias mediante una futura clasificación de consultas con n8n e inteligencia artificial.

---

## 🚀 Descripción del proyecto

**Aplicación Android de Triaje Médico con IA** es una app pensada para formar parte de un sistema de apoyo al triaje en servicios de urgencias.

La idea principal es que el paciente pueda registrarse desde el móvil, iniciar sesión y escribir el motivo por el que acude a urgencias. Esta información se enviará al backend Django, donde se guardarán los datos y, en una fase posterior, se enviará la consulta a un flujo de n8n conectado con inteligencia artificial.

La inteligencia artificial no sustituirá al personal sanitario, sino que servirá como una herramienta de apoyo para clasificar inicialmente las consultas y ayudar a que el médico pueda ver una lista más ordenada de pacientes.

---

## 🎯 Problema que busca resolver

En servicios de urgencias puede haber momentos en los que llegan muchos pacientes al mismo tiempo. En esos casos, el personal médico o de admisión tiene que revisar los motivos de consulta, valorar la gravedad inicial y decidir el orden de atención.

Aunque revisar un caso pueda parecer algo rápido, cuando hay muchos pacientes esta tarea puede acumular bastante tiempo. Por eso este proyecto plantea una forma de recoger la información inicial del paciente de manera más ordenada.

La aplicación permite que el paciente introduzca su información desde el móvil, evitando repetir datos y preparando el sistema para que el motivo de consulta pueda ser clasificado más adelante mediante inteligencia artificial.

---

## 🧩 Contexto del sistema completo

Este repositorio contiene únicamente la **aplicación Android** del proyecto.

El sistema completo está formado por varias partes:

- Aplicación Android para pacientes.
- Backend desarrollado con Django.
- Base de datos SQLite durante el desarrollo.
- Panel web para el personal médico.
- Flujo de automatización con n8n.
- Clasificación prevista mediante inteligencia artificial.

La arquitectura general prevista es:

    App Android --> Django --> Base de datos SQLite
    Django --> Panel web del médico
    Django --> n8n --> Agente de IA --> Clasificación de triaje --> Django

---

## 🔄 Flujo general del sistema

El flujo previsto del sistema completo es:

    Paciente se registra en Android
    Android envía los datos a Django
    Django crea el usuario y el paciente
    Paciente inicia sesión
    Django valida email y contraseña
    Android muestra la pantalla principal del paciente
    Paciente escribe el motivo de consulta
    Django guarda la consulta
    n8n clasifica la consulta mediante IA
    Django actualiza la prioridad
    El médico ve las consultas ordenadas en el panel web

Actualmente ya está implementada la parte de registro e inicio de sesión entre Android y Django. El envío real de consultas y la integración con n8n están previstos como siguientes pasos.

---

## ✨ Funcionalidades principales

Actualmente la aplicación Android permite:

- Registro de pacientes contra el backend Django.
- Inicio de sesión contra el backend Django.
- Validación local de campos en Android.
- Control de errores de usuario no registrado.
- Control de errores de contraseña incorrecta.
- Autocompletado del email en login después de registrarse.
- Pantalla principal personalizada con nombre, DNI y email del paciente.
- Campo preparado para escribir el motivo de consulta.
- Botón para cerrar sesión.
- Comunicación con Django mediante peticiones HTTP y JSON usando Volley.

Funcionalidades previstas:

- Enviar el motivo de consulta desde Android a Django.
- Mostrar el estado de la consulta enviada.
- Permitir modificar la consulta si el paciente se equivoca o quiere añadir síntomas.
- Permitir cancelar una consulta.
- Mostrar información relacionada con la posición o prioridad de la consulta.
- Integración completa con el sistema de triaje mediante IA.

---

## 🧠 Triaje asistido por inteligencia artificial

La aplicación Android será la encargada de recoger el motivo de consulta escrito por el paciente. Esta información no se enviará directamente a la inteligencia artificial, sino que pasará primero por Django.

La idea prevista es:

    Android --> Django --> n8n --> IA --> Clasificación --> Django

Django guardará la consulta y enviará los datos necesarios a n8n. Después, n8n se encargará de pasar el motivo de consulta a un agente de inteligencia artificial preparado con criterios de triaje.

La respuesta de la IA podría incluir una categoría, una prioridad y una observación o resumen. Django guardará esa clasificación para que el panel médico pueda mostrar las consultas de forma ordenada.

El objetivo no es tomar decisiones médicas automáticas, sino ayudar a ordenar la información y reducir el tiempo que se pierde revisando manualmente cada motivo de consulta desde cero.

---

## 🛠️ Tecnologías utilizadas

- **Java**
- **Android Studio**
- **Gradle**
- **XML Layouts**
- **Volley**
- **Peticiones HTTP**
- **JSON**
- **Django** *(backend externo del sistema)*
- **SQLite** *(base de datos usada por el backend durante el desarrollo)*
- **n8n** *(previsto para la integración con IA)*
- **Git / GitHub**

---

## 📁 Estructura del proyecto

    Triaje-androidApp/
    ├── app/
    │   └── src/
    │       └── main/
    │           ├── java/
    │           │   └── com/
    │           │       └── example/
    │           │           └── triaje/
    │           │               ├── MainActivity.java
    │           │               ├── RegisterActivity.java
    │           │               └── HomeActivity.java
    │           │
    │           ├── res/
    │           │   ├── drawable/
    │           │   ├── layout/
    │           │   ├── mipmap/
    │           │   └── values/
    │           │
    │           └── AndroidManifest.xml
    │
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

La aplicación cuenta actualmente con tres pantallas principales.

### Login - MainActivity

Pantalla inicial de la aplicación.

Permite al paciente introducir su correo electrónico y contraseña. La app valida que los campos no estén vacíos y que el email tenga un formato correcto.

Después envía los datos al backend Django mediante una petición POST.

Endpoint utilizado:

    POST http://10.0.2.2:8000/api/auth/login/

Ejemplo de JSON enviado:

    {
      "email": "marta@test.com",
      "password": "123456"
    }

Si el login es correcto, Django devuelve los datos del paciente y Android abre la pantalla principal.

Si el usuario no existe o la contraseña está mal, la aplicación muestra un mensaje de error y no permite entrar.

---

### Registro - RegisterActivity

Pantalla donde el paciente puede crear una cuenta.

Datos solicitados:

- Nombre completo.
- DNI.
- Email.
- Contraseña.

Antes de enviar los datos al backend, Android comprueba:

- Que ningún campo esté vacío.
- Que el DNI tenga 8 números y una letra.
- Que el email tenga formato válido.
- Que la contraseña tenga al menos 6 caracteres.

Endpoint utilizado:

    POST http://10.0.2.2:8000/api/auth/register/

Ejemplo de JSON enviado:

    {
      "nombre_completo": "Marta Garcia",
      "dni": "12345678A",
      "email": "marta@test.com",
      "password": "123456"
    }

Si el registro es correcto, la aplicación vuelve al login y autocompleta el email usado en el registro. La contraseña no se autocompleta por seguridad.

---

### Pantalla principal - HomeActivity

Pantalla que se muestra después de iniciar sesión correctamente.

Actualmente muestra:

- Nombre del paciente.
- DNI del paciente.
- Email del paciente.
- Campo para escribir el motivo de consulta.
- Botón para enviar consulta.
- Botón para cerrar sesión.

En un primer diseño esta pantalla volvía a pedir nombre y DNI, pero se modificó porque esos datos ya se introducen durante el registro. Ahora solo queda editable el motivo de consulta, que es lo que realmente tiene que escribir el paciente.

El envío real de la consulta al backend está previsto como siguiente paso.

---

## 🔐 Registro y login

La aplicación ya está conectada con el backend Django para registrar e iniciar sesión.

### Flujo de registro

    Usuario abre registro
    Usuario introduce nombre, DNI, email y contraseña
    Android valida los campos
    Android envía JSON a Django
    Django crea usuario y paciente
    Android muestra mensaje de éxito
    Android vuelve al login con el email autocompletado

### Flujo de login

    Usuario introduce email y contraseña
    Android valida los campos
    Android envía JSON a Django
    Django comprueba usuario y contraseña
    Django devuelve datos del paciente
    Android abre HomeActivity

Errores controlados:

- Usuario no reconocido.
- Contraseña incorrecta.
- Email con formato incorrecto.
- Campos vacíos.
- DNI con formato incorrecto.
- Contraseña demasiado corta.

---

## 🌐 Comunicación con el backend

La comunicación entre Android y Django se realiza mediante peticiones HTTP en formato JSON.

Para ello se utiliza la librería **Volley**.

Durante el desarrollo se usa un emulador de Android Studio. Por este motivo, desde Android no se llama al backend con:

    http://127.0.0.1:8000/

En su lugar se utiliza:

    http://10.0.2.2:8000/

Esto permite que el emulador Android pueda conectarse al servidor Django que se está ejecutando en el ordenador.

URLs utilizadas actualmente:

    http://10.0.2.2:8000/api/auth/register/
    http://10.0.2.2:8000/api/auth/login/

---

## ⚙️ Configuración necesaria

Para que la aplicación pueda conectarse al backend local, es necesario que el backend Django esté ejecutándose.

Desde el proyecto Django:

    python manage.py runserver

También es necesario que el proyecto Android tenga permiso de Internet en el archivo `AndroidManifest.xml`:

    <uses-permission android:name="android.permission.INTERNET" />

Durante el desarrollo, como se usa HTTP local, también se permite tráfico no cifrado:

    android:usesCleartextTraffic="true"

---

## 🔐 Datos y privacidad

Este proyecto está desarrollado como prototipo académico y técnico.

No se deben almacenar datos reales de pacientes en este repositorio.

La información sensible, como claves de API, tokens, credenciales o datos médicos reales, debe mantenerse siempre fuera del código fuente público.

No se deben subir a GitHub:

- Claves API.
- Tokens.
- Credenciales.
- Datos médicos reales.
- Información personal real de pacientes.
- Configuraciones privadas de producción.

Para una versión más avanzada se recomienda utilizar:

- `local.properties`
- Variables de entorno.
- Archivos de configuración ignorados por Git.
- `BuildConfig`.

---

## ⚙️ Instalación y ejecución

### Requisitos

- Android Studio.
- JDK compatible.
- Gradle.
- Android SDK.
- Emulador Android o dispositivo físico.
- Backend Django ejecutándose en local para probar registro y login.

### Pasos

Clonar el repositorio:

    git clone https://github.com/mmarinoa/Triaje-androidApp.git

Entrar en el proyecto:

    cd Triaje-androidApp

Abrir el proyecto con Android Studio.

Sincronizar Gradle:

    File --> Sync Project with Gradle Files

Ejecutar la aplicación en un emulador o dispositivo Android.

---

## 🧪 Pruebas actuales

Actualmente se han probado las siguientes partes:

- Registro correcto desde Android contra Django.
- Login correcto desde Android contra Django.
- Error cuando el usuario no existe.
- Error cuando la contraseña es incorrecta.
- Autocompletado del email después del registro.
- Paso de datos del paciente desde login a HomeActivity.
- Visualización del nombre, DNI y email en la pantalla principal.
- Botón de cerrar sesión.

---

## 📍 Próximos pasos

- Conectar el botón de enviar consulta con Django.
- Crear una consulta real en la base de datos desde Android.
- Crear pantalla para ver el estado de la consulta.
- Permitir modificar el motivo de consulta.
- Permitir cancelar la consulta.
- Mostrar prioridad o posición aproximada en la lista.
- Integrar Django con n8n.
- Clasificar consultas mediante inteligencia artificial.
- Mejorar la experiencia visual de las pantallas.
- Preparar una versión más cercana a producción.

---

## 👩‍💻 Autora

Proyecto desarrollado por **Marta Mariño Alvite** como parte del TFC del ciclo de **Desarrollo de Aplicaciones Multiplataforma**.
