package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model

import com.google.gson.annotations.SerializedName

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
    val fullName: String get() = "$firstName $lastName"
}

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

data class Album(
    val id: Int,
    val profileId: Int,
    val title: String,
    val description: String,
    val photos: List<String>? = emptyList()
)

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

data class SocialEvent(
    val id: Int,
    val title: String,
    @SerializedName("eventDate") val date: String,
    val customerName: String,
    val place: String,
    @SerializedName("valueStatus") val status: String
)

data class ServiceItem(
    val id: String?,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val quoteId: String
)

data class Review(
    @SerializedName("Id") val id: Int, // As noted in backend report, capitalization is inconsistent
    val content: String,
    val fullName: String,
    val socialEventDate: String,
    val rating: Double,
    val profileId: Int
)

data class ImageUploadResponse(
    val url: String,
    val secureUrl: String,
    val publicId: String
)

// Request bodies for organizer-created resources (map to the backend Create*Resource records).
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

data class CreateSocialEventRequest(
    val title: String,
    val place: String,
    val date: String,        // "yyyy-MM-dd"
    val customerName: String,
    val status: String
)
