# Funcionalidades del Usuario Organizador

Este documento consolida las funcionalidades que deberia tener el usuario **organizador** en Eventify/OrganiX, tomando como base el contenido revisado en las ramas del reporte: `feature/chapter-1`, `origin/feature/chapter-2`, `feature/chapter-3`, `feature/chapter-4`, `origin/feature/cover`, `develop` y `main`.

## 1. Objetivo del rol organizador

El organizador necesita una plataforma centralizada para gestionar eventos sociales no masivos, reducir errores de coordinacion, atender solicitudes de anfitriones, controlar presupuestos, administrar tareas, mantener comunicacion directa y fortalecer su reputacion profesional mediante perfiles, reseñas y calificaciones.

## 2. Modulos funcionales principales

### 2.1. Cuenta, autenticacion y acceso

| Funcionalidad | Descripcion | Prioridad sugerida |
| --- | --- | --- |
| Registro de organizador | Permitir que un organizador cree una cuenta profesional con sus datos basicos. | Alta |
| Inicio de sesion | Autenticar al organizador y permitir acceso seguro a la aplicacion. | Alta |
| Gestion de sesion | Mantener una sesion autenticada mediante token o mecanismo equivalente. | Alta |
| Recuperacion de acceso | Permitir recuperacion de contraseña mediante codigo/verificacion. | Media |
| Configuracion de cuenta | Permitir actualizar datos personales, preferencias y configuracion general. | Media |

### 2.2. Perfil profesional del organizador

| Funcionalidad | Descripcion | Prioridad sugerida |
| --- | --- | --- |
| Crear perfil profesional | Registrar informacion publica del organizador: nombre, descripcion, experiencia, especialidades, ubicacion y datos de contacto. | Alta |
| Editar perfil | Mantener actualizada la informacion profesional y preferencias del organizador. | Alta |
| Mostrar perfil publico | Permitir que anfitriones visualicen el perfil, experiencia, servicios y reputacion del organizador. | Alta |
| Gestionar especialidades | Registrar tipos de eventos atendidos, como bodas, cumpleaños, quinceañeros o eventos corporativos. | Media |
| Visualizar estadisticas | Mostrar metricas del perfil, eventos realizados, calificacion promedio y actividad reciente. | Media |
| Boton de contacto directo | Incluir acceso rapido a contacto externo, como WhatsApp, segun la recomendacion de validacion UX. | Media |

### 2.3. Dashboard del organizador

| Funcionalidad | Descripcion | Prioridad sugerida |
| --- | --- | --- |
| Panel principal | Mostrar resumen de eventos activos, tareas pendientes, cotizaciones y mensajes recientes. | Alta |
| Accesos rapidos | Permitir crear eventos, revisar cotizaciones, abrir mensajes y crear tareas desde el panel. | Alta |
| Alertas operativas | Mostrar incidencias, hitos importantes, vencimientos y cambios relevantes del evento. | Alta |
| Indicadores de progreso | Visualizar estado general de planificacion por evento. | Media |

### 2.4. Gestion de eventos

| Funcionalidad | Descripcion | Historia relacionada |
| --- | --- | --- |
| Registrar eventos | Crear un nuevo evento con detalles basicos para iniciar su planificacion. | US17 |
| Visualizar eventos | Listar eventos programados, activos, proximos o historicos. | US22 |
| Ver detalle de evento | Consultar informacion del evento, asistentes, responsables, proveedores, progreso y estado. | US22 |
| Editar informacion del evento | Modificar datos del evento cuando cambien condiciones, horarios o alcance. | EP03 / TS03 |
| Eliminar/cancelar evento | Retirar eventos cuando ya no correspondan o sean cancelados. | TS04 |
| Filtrar eventos por estado | Buscar eventos segun estado de avance o situacion operativa. | TS09 |
| Buscar eventos por titulo | Encontrar eventos por nombre exacto o coincidencia parcial. | TS10 |
| Buscar eventos por cliente/anfitrion | Consultar eventos asociados a un anfitrion especifico. | TS11 |
| Historial de eventos | Acceder a eventos anteriores para consulta, seguimiento o referencia. | Cap. 3 |

### 2.5. Planificacion, tareas y tablero Kanban

| Funcionalidad | Descripcion | Historia relacionada |
| --- | --- | --- |
| Gestionar lista de tareas | Crear, consultar y actualizar tareas asociadas a cada evento. | US18 |
| Tablero Kanban | Visualizar tareas por estado, como pendiente, en progreso y completada. | Cap. 3 |
| Crear tarea | Registrar responsable, fecha, prioridad y descripcion. | Cap. 3 |
| Ver detalle de tarea | Consultar subtareas, progreso, comentarios y datos asociados. | Cap. 3 |
| Actualizar estado de tarea | Cambiar el estado de una tarea y reflejar el progreso del evento. | US18 |
| Asignar responsables | Delegar tareas o funciones a colaboradores. | US20 |
| Notificar asignaciones | Avisar al colaborador cuando recibe una invitacion o responsabilidad. | US20 |
| Interaccion tactil optimizada | Usar gestos como swipe, drag and drop o touch targets adecuados para mover tareas en mobile. | Validacion UX |

### 2.6. Cronograma y calendario

| Funcionalidad | Descripcion | Historia relacionada |
| --- | --- | --- |
| Calendario de eventos | Visualizar todos los eventos programados por fecha y horario. | Cap. 3 |
| Cronograma del evento | Mostrar actividades programadas y secuencia del evento. | US21 |
| Agregar actividades | Registrar actividades dentro del cronograma. | US21 |
| Reajustar cronograma | Actualizar horarios y recalcular la secuencia cuando cambia una actividad. | US21 |
| Recordatorios automaticos | Notificar hitos importantes, fechas limite, pagos o tareas pendientes. | Cap. 1 |

### 2.7. Cotizaciones y negociacion

| Funcionalidad | Descripcion | Evidencia |
| --- | --- | --- |
| Recibir solicitudes de cotizacion | Permitir que anfitriones soliciten cotizaciones al organizador. | US24 |
| Gestionar cotizaciones | Ver solicitudes, preparar ofertas y dar seguimiento a respuestas. | Cap. 3 / Cap. 4 |
| Ver bandeja de cotizaciones | Mostrar la lista de cotizaciones asociadas al organizador. | Endpoint `/api/v1/organizers/{organizerId}/quotes` |
| Ver detalle de servicios cotizados | Consultar servicios incluidos dentro de una cotizacion. | Endpoint `/api/v1/quotes/{quoteId}/service-items` |
| Agrupar gastos por categoria | Compactar cotizaciones largas por categorias como catering, logistica o decoracion. | Validacion UX |
| Notificar aprobacion de cotizacion | Avisar en tiempo real cuando una cotizacion sea aprobada o cambie de estado. | Validacion UX |

### 2.8. Presupuesto, pagos y control financiero

| Funcionalidad | Descripcion | Historia relacionada |
| --- | --- | --- |
| Definir presupuesto | Registrar monto base del presupuesto del evento. | US19 |
| Registrar gastos | Agregar gastos vinculados al evento para controlar el uso del presupuesto. | US19 |
| Calcular saldo | Recalcular automaticamente el saldo cuando se agregan o modifican gastos. | US19 |
| Estado financiero | Visualizar montos, pagos, pendientes y avance financiero de cada evento. | Cap. 4 |
| Gestion de pagos | Dar seguimiento a pagos del anfitrion, cuotas, anticipos o pagos pendientes. | Cap. 1 |
| Alertas de pago | Notificar retrasos, proximos vencimientos o confirmaciones de pago. | Cap. 1 |

### 2.9. Comunicacion con anfitriones

| Funcionalidad | Descripcion | Historia relacionada |
| --- | --- | --- |
| Chat integrado | Comunicarse con anfitriones dentro de la plataforma. | US11 |
| Listado de conversaciones | Ver conversaciones activas desde la cuenta. | US11 |
| Historial de mensajes | Revisar conversaciones anteriores para recordar acuerdos importantes. | US12 |
| Estado de mensajes | Mostrar estados como enviado, recibido y leido. | US15 |
| Envio de archivos | Compartir PDFs, imagenes, formularios, referencias visuales o documentos importantes. | US14 |
| Previsualizar/descargar archivos | Permitir abrir o descargar archivos recibidos en el chat. | US14 |
| Notificaciones de mensajes | Avisar cuando llega un mensaje nuevo dentro de la app. | US13 |
| Notificaciones por email | Enviar correo si hay mensajes sin leer cuando el usuario no esta conectado. | US16 |
| Comunicacion bidireccional | Garantizar que anfitrion y organizador puedan coordinar desde el mismo canal. | EP02 |

### 2.10. Reseñas, reputacion y confianza

| Funcionalidad | Descripcion | Evidencia |
| --- | --- | --- |
| Visualizar reseñas recibidas | Consultar calificaciones y comentarios dejados por anfitriones. | Cap. 3 |
| Mostrar calificacion promedio | Resumir reputacion del organizador en su perfil publico. | Cap. 1 / Cap. 3 |
| Ver historial de reseñas | Acceder a comentarios de eventos organizados. | Cap. 3 |
| Publicacion de reseñas por anfitrion | Soportar que el anfitrion califique al organizador al finalizar el evento. | US26 |
| Edicion de reseñas por anfitrion | Reflejar cambios cuando el anfitrion edita una reseña previa. | US27 |

### 2.11. Suscripcion y planes

| Funcionalidad | Descripcion | Prioridad sugerida |
| --- | --- | --- |
| Ver estado de suscripcion | Mostrar plan actual, estado, renovacion y beneficios activos. | Media |
| Gestionar renovacion | Permitir administrar pagos o renovaciones del plan. | Media |
| Ver opciones de planes | Comparar beneficios disponibles para organizadores. | Media |
| Acceder a herramientas avanzadas | Desbloquear funcionalidades premium segun suscripcion adquirida. | Baja/Media |

### 2.12. Busqueda y navegacion interna

| Funcionalidad | Descripcion | Evidencia |
| --- | --- | --- |
| Buscar en calendario | Encontrar reuniones o eventos por fecha o nombre. | Cap. 3 |
| Buscar eventos | Buscar eventos por nombre, organizador o fecha. | Cap. 3 |
| Buscar dentro del perfil | Permitir al organizador encontrar sus propios eventos en "Mis eventos". | Cap. 3 |
| Navegacion inferior movil | Acceder rapidamente a Dashboard, Calendario, Eventos, Tareas, Cotizaciones, Mensajes, Perfil y Suscripcion. | Cap. 3 |
| Botones flotantes de accion | Crear eventos, enviar mensajes o cargar cotizaciones sin salir del flujo principal. | Cap. 3 |

## 3. Funcionalidades priorizadas para MVP del organizador

| Prioridad | Funcionalidad | Motivo |
| --- | --- | --- |
| 1 | Chat integrado | Es la historia con mayor puntaje y reduce errores de coordinacion. |
| 2 | Historial y estado de mensajes | Permite conservar acuerdos y saber si fueron leidos. |
| 3 | Envio de archivos | Soporta cotizaciones, formularios y referencias visuales. |
| 4 | Registro y gestion de eventos | Es la base operativa del organizador. |
| 5 | Gestion de tareas y Kanban | Ayuda a controlar ejecucion y responsabilidades. |
| 6 | Presupuestos | Permite control financiero del evento. |
| 7 | Calendario y cronograma | Reduce conflictos de horarios y olvidos. |
| 8 | Cotizaciones | Conecta la demanda del anfitrion con la oferta del organizador. |
| 9 | Dashboard | Centraliza informacion critica. |
| 10 | Perfil profesional y reseñas | Aumenta confianza, conversion y reputacion. |
| 11 | Notificaciones push/email | Mantiene seguimiento de mensajes, pagos, cotizaciones e hitos. |
| 12 | Suscripcion | Gestiona el modelo de negocio y herramientas avanzadas. |

## 4. Pantallas recomendadas para el organizador

- Login / Registro de organizador.
- Dashboard principal.
- Bandeja de cotizaciones.
- Detalle de cotizacion y servicios incluidos.
- Lista de eventos.
- Detalle de evento.
- Calendario.
- Cronograma del evento.
- Tablero Kanban de tareas.
- Crear / editar tarea.
- Detalle de tarea.
- Mensajes.
- Detalle de chat.
- Perfil del organizador.
- Editar perfil profesional.
- Reseñas.
- Suscripcion / planes.
- Configuracion.

## 5. Criterios generales de aceptacion

- El organizador puede iniciar sesion y acceder a sus modulos segun rol.
- El organizador puede registrar, consultar, actualizar y cancelar eventos.
- El organizador puede visualizar el estado general de cada evento y sus alertas.
- El organizador puede crear tareas, asignar responsables y actualizar estados.
- El organizador puede visualizar calendario y cronograma de actividades.
- El organizador puede definir presupuestos, registrar gastos y visualizar saldo.
- El organizador puede recibir, revisar y responder solicitudes de cotizacion.
- El organizador puede comunicarse con anfitriones mediante chat interno.
- El organizador puede enviar y recibir archivos desde el chat.
- El sistema conserva historial de mensajes y muestra estados de lectura.
- El sistema envia notificaciones dentro de la app y por email cuando corresponde.
- El organizador puede editar su perfil profesional y visualizar sus reseñas.
- La experiencia movil debe evitar scroll excesivo, usar touch targets adecuados y mostrar retroalimentacion inmediata.

## 6. Mejoras detectadas en validacion UX

- Agrupar los items de cotizaciones largas en categorias o acordeones para reducir desplazamiento.
- Rediseñar el Kanban movil con gestos tactiles naturales y objetivos tactiles de al menos 48x48 dp.
- Integrar notificaciones push o snackbars globales cuando se aprueba una cotizacion o cambia informacion critica.
- Agregar un boton de contacto directo a WhatsApp en el perfil del organizador.

## 7. Dependencias tecnicas identificadas

- Autenticacion mediante `POST /api/v1/authentication/sign-in`.
- Cotizaciones del organizador mediante `GET /api/v1/organizers/{organizerId}/quotes`.
- Detalle de servicios de cotizacion mediante `GET /api/v1/quotes/{quoteId}/service-items`.
- Perfiles publicos mediante `GET /api/v1/profiles`.
- Eventos sociales mediante endpoints de `SocialEventsController`.
- Reseñas mediante `ReviewsController`.
- Servicios de comunicacion para chat, historial, archivos y notificaciones.

