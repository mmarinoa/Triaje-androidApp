# 🏥📱 Aplicación Android de Triaje Médico con IA

Aplicación Android desarrollada como parte del proyecto **Sistema de triaje inteligente para urgencias**, realizado para el TFC del ciclo de **Desarrollo de Aplicaciones Multiplataforma**.

Esta aplicación es la parte móvil del sistema y está orientada al paciente. Permite que una persona pueda registrarse, iniciar sesión, enviar el motivo de su consulta, ver el estado de esa consulta, modificarla si necesita añadir información y cancelarla si ya no quiere mantenerla activa.

La aplicación se comunica con un backend desarrollado en Django. Ese backend guarda los datos, gestiona la autenticación, envía las consultas a n8n para obtener una clasificación automática mediante inteligencia artificial y devuelve a Android el estado actualizado de la consulta.

El objetivo del sistema no es sustituir al personal sanitario, sino ofrecer una herramienta de apoyo para organizar mejor las consultas pendientes y ayudar a realizar una primera clasificación orientativa.

---

## 🚀 Descripción del proyecto

**Aplicación Android de Triaje Médico con IA** es una app pensada para formar parte de un sistema de apoyo al triaje en servicios de urgencias.

La idea principal es que el paciente pueda registrarse desde el móvil, iniciar sesión y escribir el motivo por el que acude a urgencias. Esta información se envía al backend Django, donde se guarda como una consulta asociada al paciente autenticado.

Cuando se crea o modifica una consulta, Django puede enviarla a un workflow de n8n conectado con un agente de inteligencia artificial. Este agente analiza el motivo de consulta y devuelve una prioridad IA junto con una recomendación. A partir de esa prioridad, Django asigna automáticamente una categoría de triaje: Rojo, Naranja, Amarillo, Verde o Azul.

La aplicación Android muestra al paciente la información de su consulta y permite actualizar el estado para consultar si la clasificación ya se ha aplicado.

La inteligencia artificial no sustituye al personal sanitario. Funciona como una herramienta de apoyo inicial. El médico puede revisar la consulta desde un panel web, cambiar manualmente la categoría, modificar el orden de atención o marcar la consulta como atendida.

---

## 🎯 Problema que busca resolver

En servicios de urgencias puede haber momentos en los que llegan muchos pacientes al mismo tiempo. En esos casos, el personal médico o de admisión tiene que revisar los motivos de consulta, valorar la gravedad inicial y decidir el orden de atención.

Aunque revisar un caso pueda parecer algo rápido, cuando hay muchos pacientes esta tarea puede acumular bastante tiempo. Por eso este proyecto plantea una forma de recoger la información inicial del paciente de manera más ordenada.

La aplicación permite que el paciente introduzca su información desde el móvil, evitando repetir datos y preparando el sistema para que el motivo de consulta pueda clasificarse automáticamente mediante inteligencia artificial.

---

## 🧩 Contexto del sistema completo

Este repositorio contiene únicamente la **aplicación Android** del proyecto.

El sistema completo está formado por varias partes:

- Aplicación Android para pacientes.
- Backend desarrollado con Django.
- Base de datos SQLite durante el desarrollo.
- Panel web para el personal médico.
- Flujo de automatización con n8n.
- Agente de inteligencia artificial para clasificación inicial.
- Documentación Swagger/OpenAPI en el backend.

Arquitectura general del sistema:

```text
App Android → Django → Base de datos SQLite
Django → n8n → Agente IA → Prioridad IA + Recomendación → Django
Django → Panel médico web
```

La app Android no se comunica directamente con n8n ni con la inteligencia artificial. Todo pasa por Django, que actúa como punto central del sistema.

---

## 🔄 Flujo general del sistema

El flujo principal del sistema es:

```text
Paciente se registra en Android
Android envía los datos a Django
Django crea el usuario y el paciente

Paciente inicia sesión
Django valida email y contraseña
Django devuelve tokens JWT
Android guarda la sesión del paciente

Android muestra la pantalla principal
Paciente escribe el motivo de consulta
Android envía la consulta a Django con JWT

Django guarda la consulta asociada al paciente
Django envía el motivo a n8n
n8n usa un agente de IA para clasificar la consulta
n8n devuelve prioridad IA y recomendación

Django valida la prioridad recibida
Django asigna la categoría de triaje
Django guarda el histórico de categoría
Android muestra el detalle de la consulta

El paciente puede actualizar, modificar o cancelar la consulta
El médico puede revisar la consulta desde el panel web
```

Actualmente está implementada la parte de registro, login, gestión de sesión, creación de consultas, detalle de consulta, actualización de estado, modificación y cancelación desde Android. También está implementada la integración del backend con n8n y la clasificación automática mediante IA.

---

## ✨ Funcionalidades principales

Actualmente la aplicación Android permite:

- Registro de pacientes contra el backend Django.
- Inicio de sesión contra el backend Django.
- Recepción y almacenamiento de tokens JWT.
- Validación local de campos en Android.
- Control de errores de usuario no registrado.
- Control de errores de contraseña incorrecta.
- Autocompletado del email en login después de registrarse.
- Pantalla principal personalizada con nombre, DNI y email del paciente.
- Envío del motivo de consulta al backend.
- Creación de consultas autenticadas mediante JWT.
- Pantalla de detalle de consulta.
- Visualización del motivo de consulta.
- Visualización del estado de la consulta.
- Visualización de categoría de triaje cuando está disponible.
- Visualización de prioridad IA cuando está disponible.
- Visualización de recomendación generada por IA cuando está disponible.
- Actualización manual del estado de la consulta desde Android.
- Modificación del motivo de consulta.
- Reclasificación de la consulta al modificar el motivo.
- Cancelación de consulta.
- Botón para cerrar sesión.
- Comunicación con Django mediante peticiones HTTP y JSON usando Volley.
- Envío de token JWT en cabeceras `Authorization`.
- Configuración centralizada de URLs de la API mediante `ApiConfig`.
- Gestión centralizada de sesión mediante `SessionManager`.

---

## 🧠 Triaje asistido por inteligencia artificial

La aplicación Android recoge el motivo de consulta escrito por el paciente. Esta información no se envía directamente a la inteligencia artificial, sino que pasa primero por Django.

Flujo de clasificación:

```text
Android → Django → n8n → IA → Prioridad IA + Recomendación → Django → Android / Panel médico
```

Django guarda la consulta y envía los datos necesarios a n8n. Después, n8n pasa el motivo de consulta a un agente de inteligencia artificial preparado para devolver una prioridad y una recomendación.

La IA devuelve una prioridad numérica entre 1 y 5. Esa prioridad se guarda en el campo `prioridad_ia` de la consulta. A partir de ese número, Django asigna automáticamente la categoría de triaje correspondiente.

Relación entre prioridad IA y categoría:

```text
prioridad_ia = 1  →  Rojo
prioridad_ia = 2  →  Naranja
prioridad_ia = 3  →  Amarillo
prioridad_ia = 4  →  Verde
prioridad_ia = 5  →  Azul
```

El objetivo no es tomar decisiones médicas automáticas, sino ayudar a ordenar la información y reducir el tiempo que se pierde revisando manualmente cada motivo de consulta desde cero.

El médico tiene siempre la última palabra y puede modificar manualmente la categoría o el orden desde el panel médico web.

---

## 🛠️ Tecnologías utilizadas

- **Java**
- **Android Studio**
- **Gradle**
- **XML Layouts**
- **Volley**
- **Peticiones HTTP**
- **JSON**
- **JWT**
- **SharedPreferences**
- **Django** como backend externo del sistema.
- **SQLite** como base de datos usada por el backend durante el desarrollo.
- **n8n** para la automatización con IA.
- **Git / GitHub**

---

## 📁 Estructura del proyecto

```text
Triaje-androidApp/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── triaje/
│           │               ├── config/
│           │               │   └── ApiConfig.java
│           │               ├── session/
│           │               │   └── SessionManager.java
│           │               ├── MainActivity.java
│           │               ├── RegisterActivity.java
│           │               ├── HomeActivity.java
│           │               ├── DetalleConsultaActivity.java
│           │               └── EditarConsultaActivity.java
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── layout/
│           │   │   ├── activity_main.xml
│           │   │   ├── activity_register.xml
│           │   │   ├── activity_home.xml
│           │   │   ├── activity_detalle_consulta.xml
│           │   │   └── activity_editar_consulta.xml
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
```

---

## 📱 Pantallas principales

La aplicación cuenta actualmente con cinco pantallas principales.

---

### Login - MainActivity

Pantalla inicial de la aplicación.

Permite al paciente introducir su correo electrónico y contraseña. La app valida que los campos no estén vacíos y que el email tenga un formato correcto.

Después envía los datos al backend Django mediante una petición `POST`.

Endpoint utilizado:

```http
POST /api/auth/login/
```

Ejemplo de JSON enviado:

```json
{
  "email": "marta@test.com",
  "password": "123456"
}
```

Si el login es correcto, Django devuelve los datos del paciente junto con los tokens JWT. Android guarda esos tokens mediante `SessionManager` y abre la pantalla principal.

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

```http
POST /api/auth/register/
```

Ejemplo de JSON enviado:

```json
{
  "nombre_completo": "Marta Garcia",
  "dni": "12345678Z",
  "email": "marta@test.com",
  "password": "123456"
}
```

Si el registro es correcto, la aplicación vuelve al login y autocompleta el email usado en el registro. La contraseña no se autocompleta por seguridad.

---

### Pantalla principal - HomeActivity

Pantalla que se muestra después de iniciar sesión correctamente.

Muestra:

- Nombre del paciente.
- DNI del paciente.
- Email del paciente.
- Campo para escribir el motivo de consulta.
- Botón para enviar consulta.
- Botón para cerrar sesión.

En un primer diseño esta pantalla volvía a pedir nombre y DNI, pero se modificó porque esos datos ya se introducen durante el registro. Ahora solo queda editable el motivo de consulta, que es lo que realmente tiene que escribir el paciente.

Cuando el paciente pulsa el botón de enviar consulta, Android manda el motivo al backend mediante una petición `POST` protegida con JWT.

Endpoint utilizado:

```http
POST /api/consultas/
```

Ejemplo de JSON enviado:

```json
{
  "motivo": "Dolor fuerte en el pecho desde hace una hora"
}
```

Si la consulta se crea correctamente, se abre la pantalla de detalle.

---

### Detalle de consulta - DetalleConsultaActivity

Pantalla donde el paciente puede ver el resumen de la consulta enviada.

Muestra:

- Número de consulta.
- Motivo de consulta.
- Estado actual.
- Categoría de triaje, si ya está disponible.
- Prioridad IA, si ya está disponible.
- Recomendación de IA, si ya está disponible.
- Fecha de creación.
- Mensaje informativo.
- Botón para actualizar estado.
- Botón para modificar consulta.
- Botón para cancelar consulta.
- Botón para volver.

Desde esta pantalla el paciente puede actualizar manualmente el estado de la consulta. Para ello Android realiza una petición `GET` al backend.

Endpoint utilizado:

```http
GET /api/consultas/<id>/
```

También permite cancelar la consulta mediante una petición `DELETE`.

Endpoint utilizado:

```http
DELETE /api/consultas/<id>/
```

La cancelación no elimina físicamente la consulta de la base de datos, sino que cambia su estado a `cancelada`.

---

### Edición de consulta - EditarConsultaActivity

Pantalla donde el paciente puede modificar el motivo de consulta.

Al abrirse, el campo de texto aparece cubierto con el motivo actual. El paciente puede corregirlo o añadir nuevos síntomas.

Cuando pulsa guardar, Android envía una petición `PUT` al backend.

Endpoint utilizado:

```http
PUT /api/consultas/<id>/
```

Ejemplo de JSON enviado:

```json
{
  "motivo": "Dolor fuerte en el pecho y dificultad para respirar"
}
```

Si la actualización se realiza correctamente, Django puede volver a enviar la consulta a n8n para reclasificarla. Después, la aplicación vuelve a la pantalla de detalle y muestra el motivo actualizado junto con los datos actualizados de la consulta.

---

## 🔐 Registro, login y sesión

La aplicación está conectada con el backend Django para registrar e iniciar sesión.

### Flujo de registro

```text
Usuario abre registro
Usuario introduce nombre, DNI, email y contraseña
Android valida los campos
Android envía JSON a Django
Django crea usuario y paciente
Android muestra mensaje de éxito
Android vuelve al login con el email autocompletado
```

### Flujo de login

```text
Usuario introduce email y contraseña
Android valida los campos
Android envía JSON a Django
Django comprueba usuario y contraseña
Django devuelve datos del paciente y tokens JWT
Android guarda access token y refresh token
Android abre HomeActivity
```

Errores controlados:

- Usuario no reconocido.
- Contraseña incorrecta.
- Email con formato incorrecto.
- Campos vacíos.
- DNI con formato incorrecto.
- Contraseña demasiado corta.
- Sesión no iniciada o token no disponible.
- Errores de conexión con el backend.

---

## 🔑 Gestión de sesión con JWT

Cuando el login es correcto, Django devuelve dos tokens:

- `access`: token usado para autenticar las peticiones normales.
- `refresh`: token que servirá para renovar el access token en una versión posterior.

Android guarda estos datos usando `SharedPreferences` mediante la clase `SessionManager`.

Datos guardados en sesión:

- Access token.
- Refresh token.
- ID del paciente.
- Nombre del paciente.
- DNI del paciente.
- Email del paciente.

En las peticiones protegidas, Android envía el token en la cabecera:

```http
Authorization: Bearer <access_token>
```

Actualmente se usa JWT para:

- Crear consultas.
- Consultar detalle de consulta.
- Actualizar estado de consulta.
- Modificar consulta.
- Cancelar consulta.

---

## 🌐 Comunicación con el backend

La comunicación entre Android y Django se realiza mediante peticiones HTTP en formato JSON.

Para ello se utiliza la librería **Volley**.

Durante el desarrollo se usa un emulador de Android Studio. Por este motivo, desde Android no se llama al backend con:

```text
http://127.0.0.1:8000/
```

En su lugar se utiliza:

```text
http://10.0.2.2:8000/
```

Esto permite que el emulador Android pueda conectarse al servidor Django que se está ejecutando en el ordenador.

Las URLs de la API están centralizadas en la clase:

```text
ApiConfig.java
```

Esta clase contiene la URL base del servidor y las rutas principales de la API. De esta forma, si cambia la IP, el puerto o el entorno, solo es necesario modificar la URL en un único archivo.

Ejemplo de configuración:

```java
SERVER_URL = "http://10.0.2.2:8000"
```

Rutas utilizadas:

```http
/api/auth/register/
/api/auth/login/
/api/consultas/
/api/consultas/<id>/
```

---

## 📤 Datos enviados desde Android

### Registro

```json
{
  "nombre_completo": "Marta Garcia",
  "dni": "12345678Z",
  "email": "marta@test.com",
  "password": "123456"
}
```

### Login

```json
{
  "email": "marta@test.com",
  "password": "123456"
}
```

### Creación de consulta

```json
{
  "motivo": "Me duele mucho la cadera después de una caída"
}
```

### Actualización de consulta

```json
{
  "motivo": "Me duele mucho la cadera y ahora casi no puedo caminar"
}
```

---

## 📥 Datos recibidos desde Django

Cuando se crea o consulta una consulta, Android puede recibir datos como:

```json
{
  "id": 1,
  "motivo": "Me duele mucho la cadera después de una caída",
  "estado": "en_espera",
  "categoria": "Naranja",
  "prioridad_ia": 2,
  "observaciones": "Debe acudir a urgencias para valoración de posible fractura o lesión grave.",
  "fecha_creacion": "2026-04-28T09:33:35.753639+00:00"
}
```

Android utiliza estos datos para mostrar al paciente el estado y la información disponible de su consulta.

---

## ⚙️ Configuración necesaria

Para que la aplicación pueda conectarse al backend local, es necesario que el backend Django esté ejecutándose.

Desde el proyecto Django:

```bash
python manage.py runserver
```

También es necesario que el proyecto Android tenga permiso de Internet en el archivo `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Durante el desarrollo, como se usa HTTP local, también se permite tráfico no cifrado:

```xml
android:usesCleartextTraffic="true"
```

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
- Backend Django ejecutándose en local para probar registro, login y consultas.

### Pasos

Clonar el repositorio:

```bash
git clone https://github.com/mmarinoa/Triaje-androidApp.git
```

Entrar en el proyecto:

```bash
cd Triaje-androidApp
```

Abrir el proyecto con Android Studio.

Sincronizar Gradle:

```text
File → Sync Project with Gradle Files
```

Ejecutar la aplicación en un emulador o dispositivo Android.

Antes de probar login o consultas, arrancar el backend Django:

```bash
python manage.py runserver
```

---

## 🧪 Pruebas actuales

Actualmente se han probado las siguientes partes:

- Registro correcto desde Android contra Django.
- Login correcto desde Android contra Django.
- Recepción de tokens JWT.
- Guardado de sesión en Android.
- Error cuando el usuario no existe.
- Error cuando la contraseña es incorrecta.
- Autocompletado del email después del registro.
- Paso de datos del paciente desde login a HomeActivity.
- Visualización del nombre, DNI y email en la pantalla principal.
- Creación de consulta desde Android.
- Envío del token JWT en peticiones protegidas.
- Visualización de detalle de consulta.
- Actualización de estado desde Android.
- Visualización de categoría, prioridad IA y recomendación cuando están disponibles.
- Modificación de consulta desde Android.
- Reclasificación de consulta tras modificar el motivo.
- Cancelación de consulta desde Android.
- Cierre de sesión.
- Centralización de URLs de la API en `ApiConfig`.
- Gestión de sesión mediante `SessionManager`.

---

## 📍 Mejoras futuras

- Mejorar la experiencia visual de las pantallas.
- Mostrar un historial completo de consultas del paciente.
- Añadir notificaciones cuando cambie el estado de una consulta.
- Renovar automáticamente el token JWT usando el refresh token.
- Mejorar la gestión de errores de red.
- Adaptar la app para un despliegue real del backend.
- Preparar configuración segura para entornos de producción.
- Mejorar la visualización de la recomendación IA y la categoría de triaje.
- Añadir pruebas automatizadas.

---

## 👩‍💻 Autora

Proyecto desarrollado por **Marta Mariño Alvite** como parte del TFC del ciclo de **Desarrollo de Aplicaciones Multiplataforma**.
