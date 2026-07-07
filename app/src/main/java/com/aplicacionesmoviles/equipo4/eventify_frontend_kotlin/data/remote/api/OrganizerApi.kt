package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api

import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface OrganizerApi {
    // Profiles
    @POST("profiles")
    suspend fun createProfile(@Body profile: Profile): Response<Profile>

    @GET("profiles/{profileId}")
    suspend fun getProfile(@Path("profileId") profileId: Int): Response<Profile>

    @GET("profiles/email/{email}")
    suspend fun getProfileByEmail(@Path("email") email: String): Response<Profile>

    @PUT("profiles/{profileId}")
    suspend fun updateProfile(@Path("profileId") profileId: Int, @Body profile: Profile): Response<Profile>

    @POST("profiles/{profileId}/image")
    @Multipart
    suspend fun uploadProfileImage(
        @Path("profileId") profileId: Int,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // Service Catalogs
    @GET("{profileId}/service-catalogs")
    suspend fun getServiceCatalogs(@Path("profileId") profileId: Int): Response<List<ServiceCatalog>>

    @POST("{profileId}/service-catalogs")
    suspend fun createServiceCatalog(@Path("profileId") profileId: Int, @Body catalog: ServiceCatalog): Response<ServiceCatalog>

    @PUT("{profileId}/service-catalogs/{catalogId}")
    suspend fun updateServiceCatalog(
        @Path("profileId") profileId: Int,
        @Path("catalogId") catalogId: Int,
        @Body catalog: ServiceCatalog
    ): Response<ServiceCatalog>

    @DELETE("{profileId}/service-catalogs/{catalogId}")
    suspend fun deleteServiceCatalog(
        @Path("profileId") profileId: Int,
        @Path("catalogId") catalogId: Int
    ): Response<Unit>

    @POST("{profileId}/service-catalogs/images")
    @Multipart
    suspend fun uploadServiceCatalogImage(
        @Path("profileId") profileId: Int,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // Albums
    @GET("{profileId}/albums")
    suspend fun getAlbums(@Path("profileId") profileId: Int): Response<List<Album>>

    @POST("{profileId}/albums")
    suspend fun createAlbum(@Path("profileId") profileId: Int, @Body album: Album): Response<Album>

    @PUT("{profileId}/albums/{albumId}")
    suspend fun updateAlbum(
        @Path("profileId") profileId: Int,
        @Path("albumId") albumId: Int,
        @Body album: Album
    ): Response<Album>

    @DELETE("{profileId}/albums/{albumId}")
    suspend fun deleteAlbum(
        @Path("profileId") profileId: Int,
        @Path("albumId") albumId: Int
    ): Response<Unit>

    @POST("{profileId}/albums/images")
    @Multipart
    suspend fun uploadAlbumImage(
        @Path("profileId") profileId: Int,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // Quotes
    @POST("quotes")
    suspend fun createQuote(@Body body: CreateQuoteRequest): Response<Quote>

    @GET("organizers/{organizerId}/quotes")
    suspend fun getOrganizerQuotes(@Path("organizerId") organizerId: Int): Response<List<Quote>>

    @GET("quotes/{quoteId}")
    suspend fun getQuote(@Path("quoteId") quoteId: String): Response<Quote>

    @PUT("quotes/{quoteId}")
    suspend fun updateQuote(@Path("quoteId") quoteId: String, @Body quote: Quote): Response<Quote>

    @DELETE("quotes/{quoteId}")
    suspend fun deleteQuote(@Path("quoteId") quoteId: String): Response<Unit>

    @POST("quotes/{quoteId}/confirmations")
    suspend fun confirmQuote(@Path("quoteId") quoteId: String): Response<Unit>

    @POST("quotes/{quoteId}/rejections")
    suspend fun rejectQuote(@Path("quoteId") quoteId: String): Response<Unit>

    // Social Events
    @POST("social-events")
    suspend fun createSocialEvent(@Body body: CreateSocialEventRequest): Response<SocialEvent>

    @GET("social-events")
    suspend fun getSocialEvents(): Response<List<SocialEvent>>

    @DELETE("social-events/{socialEventId}")
    suspend fun deleteSocialEvent(@Path("socialEventId") socialEventId: Int): Response<Unit>

    @GET("customers/{customerName}/social-events")
    suspend fun getSocialEventsByCustomer(@Path("customerName") customerName: String): Response<List<SocialEvent>>

    // Social events owned by the logged-in organizer (profileId) — scopes the list to the
    // current organizer instead of returning every organizer's events.
    @GET("organizers/{organizerId}/social-events")
    suspend fun getSocialEventsByOrganizer(@Path("organizerId") organizerId: Int): Response<List<SocialEvent>>

    // Service Items (within a Quote)
    @GET("quotes/{quoteId}/service-items")
    suspend fun getQuoteServiceItems(@Path("quoteId") quoteId: String): Response<List<ServiceItem>>

    @POST("quotes/{quoteId}/service-items")
    suspend fun createServiceItem(@Path("quoteId") quoteId: String, @Body item: ServiceItem): Response<ServiceItem>

    @PUT("quotes/{quoteId}/service-items/{serviceItemId}")
    suspend fun updateServiceItem(
        @Path("quoteId") quoteId: String,
        @Path("serviceItemId") serviceItemId: String,
        @Body item: ServiceItem
    ): Response<ServiceItem>

    @DELETE("quotes/{quoteId}/service-items/{serviceItemId}")
    suspend fun deleteServiceItem(
        @Path("quoteId") quoteId: String,
        @Path("serviceItemId") serviceItemId: String
    ): Response<Unit>

    // Reviews
    @GET("reviews/profile/{profileId}")
    suspend fun getProfileReviews(@Path("profileId") profileId: Int): Response<List<Review>>
}
