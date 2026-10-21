# MULTITODO 📱🏥

> **Plataforma móvil nativa en Android (Jetpack Compose y Firebase) diseñada para descentralizar y optimizar la conexión entre usuarios, profesionales médicos especialistas y comercios farmacéuticos locales.**

---

## 🌟 ¿Qué es MULTITODO?
**MULTITODO** es una solución tecnológica de proximidad (*Local Health & Commerce Ecosystem*) que actúa como un puente de comunicación directo. Permite a los usuarios buscar médicos especialistas por ubicación y categoría, y conectarse instantáneamente con farmacias y comercios locales para verificar la disponibilidad de medicamentos y servicios, sin necesidad de pasarelas logísticas complejas.

---

## 🎬 Videos de Demostración (App en Acción)
Puedes ver los shorts de demostración del funcionamiento de la aplicación aquí:
- 📱 [Demo 1 - Registro y Descarga](https://youtube.com/shorts/tHMXKUkhZy0?feature=share)
- 🏥 [Demo 2 - Formar parte de la Hub](https://youtube.com/shorts/1uqRU8sNqoM?feature=share)

---

## 🚀 Características Principales
- **Exploración Jerárquica Dinámica:** Navegación fluida por provincias, ciudades, categorías y especialidades médicas.
- **Directorio de Comercios y Farmacias (`MultiStore`):** Visualización de establecimientos locales de salud y comercio general.
- **Canal de Comunicación Directa (API de WhatsApp e Intents):** Conexión inmediata vía chat y llamadas telefónicas con los profesionales y farmacias.
- **Panel de Administración (`AdminHostScreen`):** Módulo de gestión para aprobar, rechazar y administrar perfiles y formularios publicados.
- **Autenticación y Seguridad:** Control de acceso mediante Firebase Auth y reglas de seguridad granulares en Firestore y Storage.

---

## 🛠️ Stack Tecnológico & Arquitectura
El proyecto está desarrollado bajo estándares modernos de desarrollo Android:
- **Lenguaje:** 100% **Kotlin**.
- **Interfaz de Usuario:** **Jetpack Compose** + **Material 3** (Diseño declarativo y adaptativo con soporte *Edge-to-Edge*).
- **Navegación:** **Navigation Compose** (Single-Activity Architecture con paso tipado de argumentos).
- **Arquitectura y Estado:** Patrón **MVVM** con gestión reactiva mediante **StateFlow** y corrutinas.
- **Backend & Cloud (BaaS):** 
  - **Google Firebase Authentication** (Gestión de identidad).
  - **Cloud Firestore** (Base de datos NoSQL orientada a documentos en tiempo real).
  - **Firebase Storage** (Almacenamiento en la nube para multimedia, logos y avatares).
- **Librerías Clave:** **Coil** (carga asincrónica y caché de imágenes), **Core SplashScreen**.

---

## 🔒 Seguridad (Firebase Security Rules)
- **Lectura Pública Controlada:** El catálogo del directorio es accesible al público general únicamente para registros con estado `"aprobado"`.
- **Escritura Protegida:** Las operaciones de creación, actualización y eliminación de formularios requieren autenticación obligatoria (`request.auth != null`).
- **Storage Blindado:** Subida de archivos multimedia restringida a usuarios autenticados, previniendo spam o ataques de denegación de servicio.

---

## 👨‍💻 Autor
Desarrollado con pasión e ingeniería.
