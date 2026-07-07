package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Representa el perfil de un usuario (organizador o anfitrión).
 *
 * @property id Identificador único del perfil.
 * @property firstName Nombre del usuario.
 * @property lastName Apellido del usuario.
 * @property email Correo electrónico.
 * @property street Calle de la dirección.
 * @property number Número de la dirección.
 * @property city Ciudad.
 * @property postalCode Código postal.
 * @property country País.
 * @property type Tipo de usuario (ej. "ORGANIZER", "HOST").
 * @property profileImageUrl URL de la imagen de perfil.
 */
data class Profile(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val street: String?,
    val number: String?,
    val city: String?,
    val postalCode: String?,
    val country: String?,
    val type: String,
    val profileImageUrl: String? = null
) {
    /**
     * Obtiene el nombre completo concatenando nombre y apellido.
     */
    val fullName: String get() = "$firstName $lastName"
}

/**
 * Representa un servicio ofrecido por un organizador en su catálogo.
 *
 * @property id Identificador único del servicio.
 * @property profileId ID del perfil del organizador dueño del servicio.
 * @property title Título o nombre del servicio.
 * @property description Descripción detallada.
 * @property category Categoría del servicio.
 * @property priceFrom Precio mínimo base.
 * @property priceTo Precio máximo estimado.
 * @property imageUrl URL de la imagen representativa del servicio.
 */
data class ServiceCatalog(
    val id: Int,
    val profileId: Int,
    val title: String,
    val description: String,
    val category: String,
    val priceFrom: Double,
    val priceTo: Double,
    val imageUrl: String? = null
)

/**
 * Representa un álbum de fotos asociado a un perfil.
 *
 * @property id Identificador único del álbum.
 * @property profileId ID del perfil asociado.
 * @property title Título del álbum.
 * @property description Descripción del álbum.
 * @property photos Lista de URLs de las fotos contenidas.
 */
data class Album(
    val id: Int,
    val profileId: Int,
    val title: String,
    val description: String,
    val photos: List<String>? = emptyList()
)

/**
 * Representa una cotización enviada por un organizador.
 *
 * @property id Identificador de la cotización.
 * @property title Título del evento cotizado.
 * @property eventType Tipo de evento.
 * @property guestQuantity Cantidad de invitados estimados.
 * @property location Ubicación del evento.
 * @property totalPrice Monto total de la cotización.
 * @property state Estado actual (ej. "PENDING", "ACCEPTED").
 * @property eventDate Fecha programada del evento.
 * @property organizerId ID del organizador.
 * @property hostId ID del anfitrión solicitante.
 */
data class Quote(
    @SerializedName("quoteId") val id: String,
    val title: String,
    val eventType: String,
    val guestQuantity: Int,
    val location: String,
    val totalPrice: Double,
    val state: String,
    val eventDate: String,
    val organizerId: Int,
    val hostId: Int
)

/**
 * Representa un evento social programado.
 *
 * @property id Identificador del evento.
 * @property title Nombre del evento.
 * @property date Fecha del evento (formato ISO o yyyy-MM-dd).
 * @property customerName Nombre del cliente/anfitrión.
 * @property place Lugar del evento.
 * @property status Estado del evento (ej. "Active", "Completed").
 * @property organizerId ID del organizador a cargo (opcional).
 */
data class SocialEvent(
    val id: Int,
    val title: String,
    // Backend SocialEventResource emits "date" and "status" (not "eventDate"/"valueStatus").
    // Field names already match the JSON keys, so Gson maps them directly.
    val date: String,
    val customerName: String,
    val place: String,
    val status: String,
    // Owner (profileId). Nullable because legacy rows created before ownership tracking have none.
    val organizerId: Int? = null
)

/**
 * Detalle individual de un servicio dentro de una cotización.
 *
 * @property id ID del ítem (opcional).
 * @property description Descripción del ítem o tarea.
 * @property quantity Cantidad.
 * @property unitPrice Precio unitario.
 * @property totalPrice Precio total calculado.
 * @property quoteId ID de la cotización a la que pertenece.
 */
data class ServiceItem(
    val id: String?,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val quoteId: String
)

/**
 * Representa una reseña o comentario dejado por un cliente.
 *
 * @property id Identificador de la reseña.
 * @property content Texto del comentario.
 * @property fullName Nombre completo del autor.
 * @property socialEventDate Fecha en la que ocurrió el evento reseñado.
 * @property rating Calificación otorgada (ej. 1.0 a 5.0).
 * @property profileId ID del perfil reseñado.
 */
data class Review(
    @SerializedName("Id") val id: Int, // As noted in backend report, capitalization is inconsistent
    val content: String,
    val fullName: String,
    val socialEventDate: String,
    val rating: Double,
    val profileId: Int
)

/**
 * Respuesta del servidor tras la subida de una imagen.
 *
 * @property url URL pública de la imagen.
 * @property secureUrl URL segura (HTTPS) de la imagen.
 * @property publicId Identificador único del recurso en el servicio de almacenamiento (Cloudinary).
 */
data class ImageUploadResponse(
    val url: String,
    val secureUrl: String,
    val publicId: String
)

/**
 * Cuerpo de la solicitud para crear una nueva cotización.
 *
 * @property title Título descriptivo.
 * @property eventType Categoría o tipo de evento.
 * @property guestQuantity Aforo aproximado.
 * @property location Dirección del evento.
 * @property totalPrice Presupuesto total.
 * @property state Estado inicial.
 * @property eventDate Fecha del evento (ISO-8601).
 * @property organizerId ID del organizador que la crea.
 * @property hostId ID del anfitrión destino.
 */
data class CreateQuoteRequest(
    val title: String,
    val eventType: String,
    val guestQuantity: Int,
    val location: String,
    val totalPrice: Double,
    val state: String,
    val eventDate: String,   // ISO-8601, e.g. "2026-12-24T18:00:00.000Z"
    val organizerId: Int,
    val hostId: Int
)

/**
 * Cuerpo de la solicitud para crear un evento social directamente.
 *
 * @property title Título del evento.
 * @property place Ubicación.
 * @property date Fecha programada (yyyy-MM-dd).
 * @property customerName Nombre del cliente.
 * @property status Estado inicial (ej. "Active").
 * @property organizerId ID del organizador creador.
 */
data class CreateSocialEventRequest(
    val title: String,
    val place: String,
    val date: String,        // "yyyy-MM-dd"
    val customerName: String,
    val status: String,
    val organizerId: Int     // profileId of the organizer creating the event
)
