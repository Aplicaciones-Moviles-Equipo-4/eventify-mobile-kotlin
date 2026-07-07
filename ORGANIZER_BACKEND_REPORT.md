# Eventify Platform - Reporte Backend para Rol Organizador

Fecha de analisis: 2026-06-29

## 1. Objetivo

Este documento resume todo lo que necesita saber el frontend movil para integrar el flujo del **Organizador** dentro de Eventify Platform.

El organizador es el perfil encargado de:

- Crear y administrar su perfil profesional.
- Editar nombre, correo, direccion y foto de perfil.
- Publicar catalogos de servicios.
- Mostrar albumes/portafolio de eventos realizados.
- Subir imagenes reales para albumes mediante Cloudinary.
- Recibir y gestionar cotizaciones.
- Agregar items detallados a cotizaciones.
- Ver resenas recibidas.

## 2. Base URL

Base URL de produccion para el frontend:

```text
https://eventify-platform.onrender.com/api/v1
```

En local:

```text
http://localhost:8081/api/v1
```

Health check publico:

```http
GET https://eventify-platform.onrender.com/health
```

Respuesta esperada:

```json
{
  "status": "UP",
  "service": "Eventify Platform"
}
```

## 3. Autenticacion

El backend usa JWT Bearer Token.

Primero se debe iniciar sesion:

```http
POST /api/v1/authentication/sign-in
```

Request:

```json
{
  "username": "organizador_vip",
  "password": "prueba123"
}
```

Response:

```json
{
  "id": 2,
  "username": "organizador_vip",
  "token": "JWT_TOKEN"
}
```

Luego, el movil debe enviar este header en los endpoints protegidos:

```http
Authorization: Bearer <JWT_TOKEN>
```

El endpoint de registro tambien es publico:

```http
POST /api/v1/authentication/sign-up
```

Request:

```json
{
  "username": "nuevo_usuario",
  "password": "prueba123",
  "roles": ["ROLE_USER"]
}
```

Response:

```json
{
  "id": 10,
  "username": "nuevo_usuario",
  "roles": ["ROLE_USER"]
}
```

Estado de produccion al 2026-06-29:

```text
Backend Render: activo.
Health check: OK.
CORS/preflight para frontend: OK.
Login demo y sign-up: OK en produccion.
Subida de imagenes: disponible cuando Render tenga configuradas las variables de Cloudinary.
Migraciones: Flyway habilitado con baseline para bases existentes.
Edicion de perfil y foto de perfil: implementado en backend; requiere desplegar el commit correspondiente.
```

## 4. Credencial de prueba para organizador

Usuario de prueba encontrado en `seed_data.sql`:

| Usuario             | Password      | Rol IAM       |
| ------------------- | ------------- | ------------- |
| `organizador_vip` | `prueba123` | `ROLE_USER` |

Esta credencial depende del seed/fix de arranque que actualiza los usuarios demo. Si el login devuelve `Invalid password`, significa que Render todavia no tiene desplegado ese commit o la base de datos mantiene un hash anterior.

Nota: en IAM el usuario tiene `ROLE_USER`, pero a nivel de negocio el perfil organizador se identifica con:

```text
profile_type = ORGANIZER
```

Perfil seed asociado logicamente al organizador:

| Profile ID | Nombre                | Email                             | Tipo          |
| ---------- | --------------------- | --------------------------------- | ------------- |
| `2`      | `Eventos Elegantes` | `contacto@eventoselegantes.com` | `ORGANIZER` |

Importante: actualmente no existe una relacion formal entre `users.id` y `profiles.id`. El login devuelve `user.id`, pero los modulos de negocio usan `profileId`.

Para el frontend, no asumir que `user.id == profileId`. En el seed actual coincide por conveniencia (`user.id = 2` y `profileId = 2`), pero no es una regla de dominio.

## 5. Tablas que usa el organizador

### `users`

Usuario de autenticacion.

Campos relevantes:

| Campo        | Uso                          |
| ------------ | ---------------------------- |
| `id`       | ID de usuario IAM            |
| `username` | Usuario para login           |
| `password` | Password hasheado con BCrypt |

### `profiles`

Perfil publico/profesional del organizador.

Campos relevantes:

| Campo                                                                         | Uso                                                          |
| ----------------------------------------------------------------------------- | ------------------------------------------------------------ |
| `id`                                                                        | `profileId` usado por catalogos, albumes, quotes y reviews |
| `first_name`                                                                | Nombre o razon comercial                                     |
| `last_name`                                                                 | Apellido o complemento comercial                             |
| `email_address`                                                             | Email de contacto                                            |
| `profile_type`                                                              | Debe ser`ORGANIZER`                                        |
| `profile_image_url`                                                         | URL publica de la foto de perfil en Cloudinary              |
| `street_address`, `street_number`, `city`, `postal_code`, `country` | Direccion                                                    |

### `service_catalogs`

Servicios que ofrece el organizador.

Relacion:

```text
profiles 1:N service_catalogs
```

Campos:

| Campo           | Uso                      |
| --------------- | ------------------------ |
| `id`          | ID del servicio/catalogo |
| `profile_id`  | ID del organizador       |
| `title`       | Nombre del servicio      |
| `description` | Detalle                  |
| `category`    | Categoria                |
| `price_from`  | Precio minimo            |
| `price_to`    | Precio maximo            |

### `albums`

Portafolio del organizador.

Relacion:

```text
profiles 1:N albums
```

Campos:

| Campo           | Uso                |
| --------------- | ------------------ |
| `id`          | ID del album       |
| `profile_id`  | ID del organizador |
| `title`       | Titulo             |
| `description` | Descripcion        |

### `album_photos`

Fotos de cada album.

Relacion:

```text
albums 1:N album_photos
```

Campos:

| Campo         | Uso               |
| ------------- | ----------------- |
| `album_id`  | Album propietario |
| `photo_url` | URL de imagen     |

Regla de negocio:

```text
Maximo 10 fotos por album.
```

### `quotes`

Cotizaciones donde el organizador participa.

Campos relevantes:

| Campo              | Uso                                     |
| ------------------ | --------------------------------------- |
| `quote_id`       | UUID string de la cotizacion            |
| `title`          | Titulo                                  |
| `event_type`     | Tipo de evento                          |
| `guest_quantity` | Cantidad de invitados                   |
| `location`       | Ubicacion                               |
| `total_price`    | Precio total                            |
| `state`          | `PENDING`, `ACCEPTED`, `REJECTED` |
| `event_date`     | Fecha/hora                              |
| `organizer_id`   | `profileId` del organizador           |
| `host_id`        | `profileId` del cliente               |

Relacion logica:

```text
quotes.organizer_id -> profiles.id
```

### `service_items`

Detalle de servicios dentro de una cotizacion.

Relacion:

```text
quotes 1:N service_items
```

Campos:

| Campo               | Uso                    |
| ------------------- | ---------------------- |
| `service_item_id` | UUID string del item   |
| `quote_id`        | Cotizacion propietaria |
| `description`     | Descripcion del item   |
| `quantity`        | Cantidad               |
| `unit_price`      | Precio unitario        |
| `total_price`     | Total del item         |

### `reviews`

Resenas recibidas por el organizador.

Relacion logica:

```text
reviews.profile_id -> profiles.id
```

Campos:

| Campo                 | Uso                         |
| --------------------- | --------------------------- |
| `id`                | ID de resena                |
| `profile_id`        | ID del organizador evaluado |
| `social_event_id`   | Evento relacionado          |
| `content`           | Comentario                  |
| `rating`            | Calificacion 1 a 5          |
| `full_name`         | Autor                       |
| `social_event_date` | Fecha del evento            |

## 6. Flujo recomendado para mobile

### Flujo 1: Login del organizador

1. Llamar a:

```http
POST /authentication/sign-in
```

2. Guardar:

```text
token
user.id
username
```

3. Agregar el token al interceptor HTTP.
4. Usar `profileId = 2` para la cuenta seed del organizador, o resolver el perfil por email:

```http
GET /profiles/email/contacto@eventoselegantes.com
```

### Flujo 2: Obtener perfil organizador

El backend no tiene endpoint `me/profile`, por lo que hay dos opciones:

Opcion actual:

```http
GET /profiles/email/{email}
```

O:

```http
GET /profiles/{profileId}
```

Para los datos seed, el organizador usa:

```text
profileId = 2
```

Opcion recomendada a futuro:

```http
GET /me/profile
```

Editar perfil:

```http
PUT /profiles/{profileId}
```

Subir foto de perfil:

```http
POST /profiles/{profileId}/image
Content-Type: multipart/form-data
```

Campo del formulario:

```text
file = imagen JPG/PNG/WebP/etc.
```

### Flujo 3: Gestionar catalogo de servicios

Listar servicios:

```http
GET /{profileId}/service-catalogs
```

Crear servicio:

```http
POST /{profileId}/service-catalogs
```

Actualizar servicio:

```http
PUT /{profileId}/service-catalogs/{catalogId}
```

Eliminar servicio:

```http
DELETE /{profileId}/service-catalogs/{catalogId}
```

### Flujo 4: Gestionar albumes

Listar albumes:

```http
GET /{profileId}/albums
```

Crear album:

```http
POST /{profileId}/albums
```

Actualizar album:

```http
PUT /{profileId}/albums/{albumId}
```

Eliminar album:

```http
DELETE /{profileId}/albums/{albumId}
```

Subir imagen para album:

```http
POST /{profileId}/albums/images
Content-Type: multipart/form-data
Authorization: Bearer <JWT_TOKEN>
```

Campo del formulario:

```text
file = imagen JPG/PNG/WebP/etc.
```

Respuesta:

```json
{
  "url": "http://res.cloudinary.com/.../image/upload/...",
  "secureUrl": "https://res.cloudinary.com/.../image/upload/...",
  "publicId": "eventify/albums/2/abc123"
}
```

Luego el frontend debe usar `secureUrl` dentro del arreglo `photos` al crear o actualizar un album.

### Flujo 5: Ver cotizaciones del organizador

```http
GET /organizers/{organizerId}/quotes
```

Donde:

```text
organizerId = profileId del organizador
```

Para seed:

```text
organizerId = 2
```

### Flujo 6: Agregar items a una cotizacion

Listar items:

```http
GET /quotes/{quoteId}/service-items
```

Crear item:

```http
POST /quotes/{quoteId}/service-items
```

Actualizar item:

```http
PUT /quotes/{quoteId}/service-items/{serviceItemId}
```

Eliminar item:

```http
DELETE /quotes/{quoteId}/service-items/{serviceItemId}
```

### Flujo 7: Ver resenas recibidas

```http
GET /reviews/profile/{profileId}
```

Para seed:

```text
GET /reviews/profile/2
```

## 7. Endpoints especificos del organizador

Todos llevan prefijo:

```text
/api/v1
```

### Perfil

```http
POST /profiles
GET  /profiles/{profileId}
GET  /profiles/email/{email}
GET  /profiles
PUT  /profiles/{profileId}
POST /profiles/{profileId}/image
```

### Catalogo de servicios

```http
POST   /{profileId}/service-catalogs
GET    /{profileId}/service-catalogs
GET    /{profileId}/service-catalogs/{catalogId}
PUT    /{profileId}/service-catalogs/{catalogId}
DELETE /{profileId}/service-catalogs/{catalogId}
```

### Albumes

```http
POST   /{profileId}/albums
GET    /{profileId}/albums
GET    /{profileId}/albums/{albumId}
POST   /{profileId}/albums/images
PUT    /{profileId}/albums/{albumId}
DELETE /{profileId}/albums/{albumId}
```

### Cotizaciones

```http
GET  /organizers/{organizerId}/quotes
GET  /quotes/{quoteId}
PUT  /quotes/{quoteId}
POST /quotes/{quoteId}/confirmations
POST /quotes/{quoteId}/rejections
```

Nota: aunque existen endpoints para confirmar/rechazar cotizaciones, por logica de negocio normalmente esa accion corresponderia al cliente/host, no al organizador. El backend no restringe esto por rol actualmente.

### Items de cotizacion

```http
GET    /quotes/{quoteId}/service-items
POST   /quotes/{quoteId}/service-items
PUT    /quotes/{quoteId}/service-items/{serviceItemId}
DELETE /quotes/{quoteId}/service-items/{serviceItemId}
```

### Resenas

```http
GET /reviews/profile/{profileId}
GET /reviews/{reviewId}
GET /reviews
```

## 8. Payloads listos para mobile

### Crear perfil organizador

```http
POST /api/v1/profiles
```

```json
{
  "firstName": "Eventos",
  "lastName": "Elegantes",
  "email": "contacto@eventoselegantes.com",
  "street": "Calle Las Begonias",
  "number": "456",
  "city": "Lima",
  "postalCode": "15047",
  "country": "Peru",
  "type": "ORGANIZER"
}
```

### Actualizar perfil organizador

```http
PUT /api/v1/profiles/2
```

```json
{
  "firstName": "Eventos",
  "lastName": "Elegantes",
  "email": "contacto@eventoselegantes.com",
  "street": "Calle Las Begonias",
  "number": "456",
  "city": "Lima",
  "postalCode": "15047",
  "country": "Peru",
  "profileImageUrl": "https://res.cloudinary.com/demo/image/upload/v123/eventify/profiles/2/avatar.jpg"
}
```

Response:

```json
{
  "id": 2,
  "firstName": "Eventos",
  "lastName": "Elegantes",
  "fullName": "Eventos Elegantes",
  "email": "contacto@eventoselegantes.com",
  "street": "Calle Las Begonias",
  "number": "456",
  "city": "Lima",
  "postalCode": "15047",
  "country": "Peru",
  "fullAddress": "Calle Las Begonias 456, Lima 15047, Peru",
  "type": "ORGANIZER",
  "profileImageUrl": "https://res.cloudinary.com/demo/image/upload/v123/eventify/profiles/2/avatar.jpg"
}
```

### Subir foto de perfil

```http
POST /api/v1/profiles/2/image
Content-Type: multipart/form-data
```

Form data:

```text
file: <archivo-imagen>
```

Response:

```json
{
  "url": "http://res.cloudinary.com/demo/image/upload/v123/eventify/profiles/2/avatar.jpg",
  "secureUrl": "https://res.cloudinary.com/demo/image/upload/v123/eventify/profiles/2/avatar.jpg",
  "publicId": "eventify/profiles/2/avatar"
}
```

Este endpoint tambien guarda automaticamente `secureUrl` en `profileImageUrl`.

### Crear servicio del catalogo

```http
POST /api/v1/2/service-catalogs
```

```json
{
  "profileId": 2,
  "title": "Catering Gourmet",
  "description": "Menu de 5 tiempos con maridaje",
  "category": "Alimentacion",
  "priceFrom": 50.0,
  "priceTo": 150.0
}
```

Response:

```json
{
  "id": 1,
  "profileId": 2,
  "title": "Catering Gourmet",
  "description": "Menu de 5 tiempos con maridaje",
  "category": "Alimentacion",
  "priceFrom": 50.0,
  "priceTo": 150.0
}
```

### Crear album

```http
POST /api/v1/2/albums
```

```json
{
  "profileId": 2,
  "title": "Boda Real 2025",
  "description": "Fotos de nuestro evento mas grande del ano pasado",
  "photos": [
    "https://example.com/foto1.jpg",
    "https://example.com/foto2.jpg"
  ]
}
```

Response:

```json
{
  "id": 1,
  "profileId": 2,
  "title": "Boda Real 2025",
  "description": "Fotos de nuestro evento mas grande del ano pasado",
  "photos": [
    "https://example.com/foto1.jpg",
    "https://example.com/foto2.jpg"
  ]
}
```

### Subir imagen para album

```http
POST /api/v1/2/albums/images
Content-Type: multipart/form-data
```

Form data:

```text
file: <archivo-imagen>
```

Response:

```json
{
  "url": "http://res.cloudinary.com/demo/image/upload/v123/eventify/albums/2/foto.jpg",
  "secureUrl": "https://res.cloudinary.com/demo/image/upload/v123/eventify/albums/2/foto.jpg",
  "publicId": "eventify/albums/2/foto"
}
```

### Listar cotizaciones del organizador

```http
GET /api/v1/organizers/2/quotes
```

Response:

```json
[
  {
    "quoteId": "UUID-STRING",
    "title": "Cotizacion Buffet",
    "eventType": "WEDDING",
    "guestQuantity": 100,
    "location": "Miraflores",
    "totalPrice": 5000.0,
    "state": "PENDING",
    "eventDate": "2026-12-24T18:00:00.000Z",
    "organizerId": 2,
    "hostId": 1
  }
]
```

### Crear item para cotizacion

```http
POST /api/v1/quotes/{quoteId}/service-items
```

```json
{
  "description": "Decoracion floral premium",
  "quantity": 1,
  "unitPrice": 1200.0,
  "totalPrice": 1200.0,
  "quoteId": "UUID-STRING"
}
```

Response:

```json
{
  "id": "ITEM-UUID",
  "description": "Decoracion floral premium",
  "quantity": 1,
  "unitPrice": 1200.0,
  "totalPrice": 1200.0,
  "quoteId": "UUID-STRING"
}
```

### Ver resenas del organizador

```http
GET /api/v1/reviews/profile/2
```

Response:

```json
[
  {
    "Id": 1,
    "content": "Excelente servicio, la comida estuvo increible.",
    "fullName": "Juan Perez",
    "socialEventDate": "2026-12-24T00:00:00.000Z",
    "rating": 5,
    "profileId": 2
  }
]
```

Nota: el campo de respuesta esta definido como `Id` con mayuscula inicial en el record `ReviewResource`.

## 9. Estados y enums relevantes

### Tipo de perfil

```text
ORGANIZER
HOSTER
```

### Tipos de evento para cotizaciones

```text
CONFERENCE
BIRTHDAY
WEDDING
GRADUATION
```

### Estado de cotizacion

```text
PENDING
ACCEPTED
REJECTED
```

### Estado de evento social

El backend lo maneja como texto libre. En seed se usa:

```text
Active
```

## 10. Consideraciones importantes para el frontend

1. El `organizerId` usado en cotizaciones es el `profileId`, no el `userId`.
2. No existe endpoint para obtener el perfil del usuario autenticado tipo `/me/profile`; se debe usar `profileId` o buscar por email.
3. Si se usa el seed, el organizador principal es `profileId = 2`.
4. Los endpoints de catalogos y albumes validan que el recurso pertenezca al `profileId` de la URL.
5. Las imagenes de albumes y la foto de perfil son URLs; el backend sube el archivo a Cloudinary y devuelve `secureUrl`.
6. El backend requiere JWT para casi todos los endpoints.
7. No guardar `DB_URL`, `DB_USER`, `DB_PASSWORD` ni `JWT_SECRET` en la app movil.
8. El frontend nunca se conecta directo a MySQL. El flujo correcto es `Frontend -> Backend Render -> Base de datos`.
9. Configurar la URL base sin barra final para evitar dobles slash accidentales: `https://eventify-platform.onrender.com/api/v1`.
10. Para web/mobile con navegador, CORS ya permite `GET`, `POST`, `PUT`, `DELETE` y headers como `Authorization` y `Content-Type`.
11. Para subir imagenes, usar `multipart/form-data`; no convertir archivos a Base64.

## 11. Modelo sugerido para mobile

### OrganizerProfile

```json
{
  "id": 2,
  "firstName": "Eventos",
  "lastName": "Elegantes",
  "fullName": "Eventos Elegantes",
  "email": "contacto@eventoselegantes.com",
  "street": "Calle Las Begonias",
  "number": "456",
  "city": "Lima",
  "postalCode": "15047",
  "country": "Peru",
  "fullAddress": "Calle Las Begonias 456, Lima, 15047, Peru",
  "type": "ORGANIZER",
  "profileImageUrl": "https://res.cloudinary.com/demo/image/upload/v123/eventify/profiles/2/avatar.jpg"
}
```

### ServiceCatalog

```json
{
  "id": 1,
  "profileId": 2,
  "title": "Catering Gourmet",
  "description": "Menu de 5 tiempos con maridaje",
  "category": "Alimentacion",
  "priceFrom": 50.0,
  "priceTo": 150.0
}
```

### Album

```json
{
  "id": 1,
  "profileId": 2,
  "title": "Boda Real 2025",
  "description": "Fotos de nuestro evento mas grande del ano pasado",
  "photos": [
    "https://example.com/foto1.jpg"
  ]
}
```

### Quote

```json
{
  "quoteId": "UUID-STRING",
  "title": "Cotizacion Buffet",
  "eventType": "WEDDING",
  "guestQuantity": 100,
  "location": "Miraflores",
  "totalPrice": 5000.0,
  "state": "PENDING",
  "eventDate": "2026-12-24T18:00:00.000Z",
  "organizerId": 2,
  "hostId": 1
}
```

### ServiceItem

```json
{
  "id": "ITEM-UUID",
  "description": "Decoracion floral premium",
  "quantity": 1,
  "unitPrice": 1200.0,
  "totalPrice": 1200.0,
  "quoteId": "UUID-STRING"
}
```

### Review

```json
{
  "Id": 1,
  "content": "Excelente servicio",
  "fullName": "Juan Perez",
  "socialEventDate": "2026-12-24T00:00:00.000Z",
  "rating": 5,
  "profileId": 2
}
```

## 12. Recomendaciones de mejora backend para organizador

Para que la integracion movil sea mas robusta, se recomienda:

1. Relacionar `users` con `profiles`:

```text
profiles.user_id -> users.id
```

2. Agregar endpoint:

```http
GET /api/v1/me/profile
```

3. Agregar roles de negocio o permisos mas claros:

```text
ROLE_ORGANIZER
ROLE_HOSTER
ROLE_ADMIN
```

4. Separar acciones de cliente y organizador en cotizaciones:

```text
Organizador: crea/edita items y envia cotizacion.
Cliente: acepta o rechaza cotizacion.
```

5. Agregar endpoint para eliminar imagenes de Cloudinary cuando se quiten del album.
6. Agregar migraciones futuras para cambios de esquema en `src/main/resources/db/migration`.

## 13. Despliegue y variables sensibles

Las credenciales de base de datos y secretos JWT deben vivir solo como variables de entorno en Render. No deben guardarse en el frontend ni en documentacion compartida.

Variables esperadas por el backend en produccion:

```text
DB_URL
DB_USER
DB_PASSWORD
JWT_SECRET
PORT
SPRING_PROFILES_ACTIVE=prod
CLOUDINARY_CLOUD_NAME
CLOUDINARY_API_KEY
CLOUDINARY_API_SECRET
CLOUDINARY_ALBUM_FOLDER
```

Notas:

```text
SPRING_PROFILES_ACTIVE debe ser prod en Render.
CLOUDINARY_ALBUM_FOLDER puede quedarse como eventify/albums si no se configura.
Flyway esta habilitado y reemplaza ddl-auto=update. En una base existente usa baseline-on-migrate para no recrear tablas.
Las migraciones nuevas deben agregarse con el formato V3__descripcion.sql, V4__descripcion.sql, etc.
```

Backend desplegado en Render:

```text
https://eventify-platform.onrender.com
```

Health check recomendado en Render:

```text
/health
```
ya no ewstuve aqui  
