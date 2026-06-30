# Eventify Platform - Reporte Backend

Fecha de analisis: 2026-06-26

## 1. Resumen general

El proyecto es un backend construido con **Spring Boot 3.5**, **Java 24**, **Spring Security**, **JWT**, **Spring Data JPA/Hibernate** y **MySQL**.

La arquitectura esta organizada por contextos de dominio:

- `iam`: autenticacion, usuarios, roles y JWT.
- `profiles`: perfiles, catalogos de servicios y albumes.
- `planning`: eventos sociales, cotizaciones e items de cotizacion.
- `operation`: resenas/calificaciones.
- `shared`: auditoria, manejo de errores, OpenAPI y utilidades comunes.

Archivos principales:

- App principal: `src/main/java/com/eventify/platform/EventifyPlatformApplication.java`
- Dependencias Maven: `pom.xml`
- Configuracion base: `src/main/resources/application.properties`
- Configuracion dev: `src/main/resources/application-dev.properties`
- Configuracion prod: `src/main/resources/application-prod.properties`
- Seed de prueba: `seed_data.sql`

## 2. Stack tecnico

- Lenguaje: Java 24
- Framework: Spring Boot 3.5.0
- Persistencia: Spring Data JPA + Hibernate
- Base de datos: MySQL
- Seguridad: Spring Security + JWT Bearer Token
- Hashing de passwords: BCrypt
- Documentacion API: SpringDoc OpenAPI / Swagger
- Build: Maven
- Deploy: Dockerfile preparado para perfil `prod`

## 3. Configuracion del backend

### Puerto

El puerto se configura asi:

```properties
server.port=${PORT:8081}
```

En local, si no existe `PORT`, levanta en:

```text
http://localhost:8081
```

### Base URL de produccion encontrada

En la documentacion del repo aparece:

```text
https://eventify-platform.onrender.com/api/v1/
```

### Swagger/OpenAPI

Endpoints de documentacion:

```text
/swagger-ui/index.html
/v3/api-docs
```

## 4. Seguridad y autenticacion

La API usa autenticacion stateless con JWT.

Endpoints publicos:

```text
POST /api/v1/authentication/sign-in
POST /api/v1/authentication/sign-up
/swagger-ui/**
/v3/api-docs/**
```

Todos los demas endpoints requieren:

```http
Authorization: Bearer <JWT_TOKEN>
```

### Login

```http
POST /api/v1/authentication/sign-in
```

Request:

```json
{
  "username": "cliente_pro",
  "password": "prueba123"
}
```

Response:

```json
{
  "id": 1,
  "username": "cliente_pro",
  "token": "eyJhbGciOiJIUzI1NiJ..."
}
```

### Registro

```http
POST /api/v1/authentication/sign-up
```

Request:

```json
{
  "username": "usuario123",
  "password": "password123",
  "roles": ["ROLE_USER"]
}
```

Roles disponibles:

```text
ROLE_USER
ROLE_ADMIN
ROLE_INSTRUCTOR
```

## 5. Credenciales y variables necesarias

### Credenciales de prueba para el frontend movil

El archivo `seed_data.sql` contiene usuarios de prueba.

Password para todos:

```text
prueba123
```

Usuarios:

| Usuario | Password | Rol |
|---|---|---|
| `cliente_pro` | `prueba123` | `ROLE_USER` |
| `organizador_vip` | `prueba123` | `ROLE_USER` |
| `admin_eventify` | `prueba123` | `ROLE_ADMIN` |

### Variables de entorno del backend

Para produccion:

```env
SPRING_PROFILES_ACTIVE=prod
PORT=8080
DB_URL=jdbc:mysql://...
DB_USER=...
DB_PASSWORD=...
JWT_SECRET=...
```

Para desarrollo, si no se configuran variables:

```properties
DB_URL=jdbc:mysql://localhost:3306/eventify-os?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=
```

Nota importante: el `Dockerfile` menciona variables `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`, pero la aplicacion realmente lee `DB_URL`, `DB_USER` y `DB_PASSWORD`. Esa diferencia puede causar errores de deploy.

### Que NO debe ir en la app movil

El frontend movil no debe incluir:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- credenciales reales de infraestructura

La app movil solo necesita:

- Base URL de la API.
- Endpoint de login.
- JWT recibido al iniciar sesion.
- Header `Authorization: Bearer <token>`.

## 6. Tablas de base de datos

La estrategia de nombres convierte entidades a `snake_case` y pluraliza tablas. Ejemplo: `User` -> `users`.

### `users`

Usuarios de autenticacion.

| Campo | Tipo aproximado | Descripcion |
|---|---|---|
| `id` | BIGINT | PK autoincremental |
| `username` | VARCHAR(50) | Unico |
| `password` | VARCHAR(120) | Hash BCrypt |
| `created_at` | DATETIME | Auditoria |
| `updated_at` | DATETIME | Auditoria |

### `roles`

Roles del sistema.

| Campo | Tipo aproximado | Descripcion |
|---|---|---|
| `id` | BIGINT | PK autoincremental |
| `name` | VARCHAR(20) | Enum: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_INSTRUCTOR` |

### `user_roles`

Tabla pivote de usuarios y roles.

| Campo | Relacion |
|---|---|
| `user_id` | FK logica a `users.id` |
| `role_id` | FK logica a `roles.id` |

Relacion:

```text
users N:M roles
```

### `profiles`

Perfiles de usuario de negocio.

| Campo | Descripcion |
|---|---|
| `id` | PK |
| `first_name` | Nombre |
| `last_name` | Apellido |
| `email_address` | Email unico |
| `profile_type` | `ORGANIZER` o `HOSTER` |
| `street_address` | Direccion |
| `street_number` | Numero |
| `city` | Ciudad |
| `postal_code` | Codigo postal |
| `country` | Pais |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Nota importante: actualmente `profiles` no tiene relacion directa con `users`.

### `service_catalogs`

Catalogos de servicios de un perfil organizador.

| Campo | Descripcion |
|---|---|
| `id` | PK |
| `profile_id` | Perfil propietario |
| `title` | Titulo |
| `description` | Descripcion |
| `category` | Categoria |
| `price_from` | Precio minimo |
| `price_to` | Precio maximo |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Relacion:

```text
profiles 1:N service_catalogs
```

### `albums`

Albumes de fotos de un perfil.

| Campo | Descripcion |
|---|---|
| `id` | PK |
| `profile_id` | Perfil propietario |
| `title` | Titulo |
| `description` | Descripcion |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Relacion:

```text
profiles 1:N albums
```

### `album_photos`

URLs de fotos de albumes.

| Campo | Descripcion |
|---|---|
| `album_id` | Album propietario |
| `photo_url` | URL de imagen |

Relacion:

```text
albums 1:N album_photos
```

Regla de negocio: un album puede contener maximo 10 fotos.

### `social_events`

Eventos sociales.

| Campo | Descripcion |
|---|---|
| `id` | PK |
| `title` | Titulo |
| `event_date` | Fecha del evento |
| `customer_name` | Nombre del cliente |
| `place` | Lugar |
| `value_status` | Estado |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

### `quotes`

Cotizaciones.

| Campo | Descripcion |
|---|---|
| `quote_id` | PK tipo UUID string |
| `title` | Titulo |
| `event_type` | `CONFERENCE`, `BIRTHDAY`, `WEDDING`, `GRADUATION` |
| `guest_quantity` | Cantidad de invitados |
| `location` | Lugar |
| `total_price` | Precio total |
| `state` | `PENDING`, `ACCEPTED`, `REJECTED` |
| `event_date` | Fecha/hora del evento |
| `organizer_id` | ID logico de perfil organizador |
| `host_id` | ID logico de perfil cliente/host |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Relaciones logicas:

```text
quotes.organizer_id -> profiles.id
quotes.host_id -> profiles.id
```

Estas relaciones no estan modeladas como `@ManyToOne`; son columnas `Long`.

### `service_items`

Items de una cotizacion.

| Campo | Descripcion |
|---|---|
| `service_item_id` | PK tipo UUID string |
| `description` | Descripcion |
| `quantity` | Cantidad |
| `unit_price` | Precio unitario |
| `total_price` | Total |
| `quote_id` | ID de cotizacion |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Relacion logica:

```text
quotes 1:N service_items
```

### `reviews`

Resenas/calificaciones.

| Campo | Descripcion |
|---|---|
| `id` | PK |
| `content` | Comentario |
| `rating` | Calificacion de 1 a 5 |
| `full_name` | Nombre del autor |
| `social_event_date` | Fecha del evento |
| `profile_id` | Perfil evaluado |
| `social_event_id` | Evento relacionado |
| `created_at` | Auditoria |
| `updated_at` | Auditoria |

Relaciones logicas:

```text
reviews.profile_id -> profiles.id
reviews.social_event_id -> social_events.id
```

## 7. Mapa de relaciones

```text
users
  N:M roles
  mediante user_roles

profiles
  1:N service_catalogs
  1:N albums

albums
  1:N album_photos

profiles
  1:N quotes como organizer_id
  1:N quotes como host_id

quotes
  1:N service_items

profiles
  1:N reviews

social_events
  1:N reviews
```

## 8. Endpoints principales

Todos los endpoints estan bajo:

```text
/api/v1
```

### IAM

```http
POST /authentication/sign-in
POST /authentication/sign-up
GET  /roles
GET  /users
GET  /users/{userId}
POST /users
```

### Profiles

```http
POST /profiles
GET  /profiles
GET  /profiles/{profileId}
GET  /profiles/email/{email}
```

### Albums

```http
POST   /{profileId}/albums
GET    /{profileId}/albums
GET    /{profileId}/albums/{albumId}
PUT    /{profileId}/albums/{albumId}
DELETE /{profileId}/albums/{albumId}
```

### Service Catalogs

```http
POST   /{profileId}/service-catalogs
GET    /{profileId}/service-catalogs
GET    /{profileId}/service-catalogs/{catalogId}
PUT    /{profileId}/service-catalogs/{catalogId}
DELETE /{profileId}/service-catalogs/{catalogId}
```

### Social Events

```http
POST   /social-events
GET    /social-events
PUT    /social-events/{socialEventId}
DELETE /social-events/{socialEventId}
GET    /social-events/status/{status}
GET    /social-events/title/{title}
GET    /customers/{customerName}/social-events
```

### Quotes

```http
POST   /quotes
GET    /quotes/{quoteId}
PUT    /quotes/{quoteId}
DELETE /quotes/{quoteId}
POST   /quotes/{quoteId}/confirmations
POST   /quotes/{quoteId}/rejections
GET    /organizers/{organizerId}/quotes
```

### Quote Service Items

```http
GET    /quotes/{quoteId}/service-items
POST   /quotes/{quoteId}/service-items
PUT    /quotes/{quoteId}/service-items/{serviceItemId}
DELETE /quotes/{quoteId}/service-items/{serviceItemId}
```

### Reviews

```http
POST /reviews
GET  /reviews
GET  /reviews/{reviewId}
GET  /reviews/profile/{profileId}
PUT  /reviews/{reviewId}
```

## 9. Payloads utiles para mobile

### Crear perfil

```http
POST /api/v1/profiles
```

```json
{
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan@example.com",
  "street": "Av. Primavera",
  "number": "123",
  "city": "Lima",
  "postalCode": "15036",
  "country": "Peru",
  "type": "HOSTER"
}
```

Tipos de perfil:

```text
ORGANIZER
HOSTER
```

### Crear evento social

```http
POST /api/v1/social-events
```

```json
{
  "title": "Boda Ana y Luis",
  "place": "Hotel Westin",
  "date": "2026-12-24",
  "customerName": "Juan Perez",
  "status": "Active"
}
```

### Crear cotizacion

```http
POST /api/v1/quotes
```

```json
{
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

### Crear item de cotizacion

```http
POST /api/v1/quotes/{quoteId}/service-items
```

```json
{
  "description": "Decoracion floral",
  "quantity": 1,
  "unitPrice": 1200.0,
  "totalPrice": 1200.0,
  "quoteId": "UUID-STRING"
}
```

### Crear resena

```http
POST /api/v1/reviews
```

```json
{
  "content": "Excelente servicio",
  "fullName": "Juan Perez",
  "socialEventDate": "2026-12-24T00:00:00.000Z",
  "rating": 5,
  "profileId": 2,
  "socialEventId": 1
}
```

## 10. Recomendaciones para integracion movil

1. Hacer login con `/authentication/sign-in`.
2. Guardar el token JWT en almacenamiento seguro:
   - Flutter: `flutter_secure_storage`.
   - Android nativo: EncryptedSharedPreferences/DataStore seguro.
3. Crear un interceptor HTTP que agregue:

```http
Authorization: Bearer <token>
```

4. Manejar codigos:
   - `200`: OK.
   - `201`: creado.
   - `204`: eliminado sin contenido.
   - `400`: request invalido.
   - `401`: token ausente/invalido.
   - `403`: acceso prohibido.
   - `404`: recurso no encontrado.
5. No usar credenciales de base de datos en el frontend.
6. Resolver la relacion usuario-perfil. Actualmente el backend devuelve `user.id` al hacer login, pero los modulos de negocio necesitan `profileId`.

## 11. Riesgos y observaciones tecnicas

- No hay migraciones versionadas con Flyway o Liquibase.
- `spring.jpa.hibernate.ddl-auto=update` puede ser riesgoso en produccion.
- `JWT_SECRET` tiene un valor por defecto inseguro si no se configura como variable de entorno.
- `users` y `profiles` no estan relacionados.
- Algunas relaciones son solo IDs logicos y no foreign keys JPA fuertes.
- El `Dockerfile` documenta nombres de variables diferentes a los que realmente usa Spring.
- Se mezclan fechas `LocalDate` y `Date`, por lo que el mobile debe cuidar formatos:
  - Social events: `yyyy-MM-dd`
  - Quotes/reviews: ISO date-time, por ejemplo `2026-12-24T18:00:00.000Z`

## 12. Siguiente mejora recomendada

La mejora mas importante para integrar bien con mobile es agregar una relacion formal:

```text
profiles.user_id -> users.id
```

Y exponer un endpoint:

```http
GET /api/v1/me/profile
```

Con eso la app podria iniciar sesion, obtener el JWT y saber inmediatamente que `profileId` usar en catalogos, albumes, cotizaciones, eventos y resenas.
