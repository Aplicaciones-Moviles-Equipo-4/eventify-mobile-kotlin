package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.LocalStore
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.NotificationType
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.SessionManager
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.NetworkModule
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.*
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class OrganizerViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    
    var profile by mutableStateOf<Profile?>(null)
    var serviceCatalogs by mutableStateOf<List<ServiceCatalog>>(emptyList())
    var albums by mutableStateOf<List<Album>>(emptyList())
    var quotes by mutableStateOf<List<Quote>>(emptyList())
    var socialEvents by mutableStateOf<List<SocialEvent>>(emptyList())
    var reviews by mutableStateOf<List<Review>>(emptyList())
    
    // For Quote Detail
    var currentQuoteItems by mutableStateOf<List<ServiceItem>>(emptyList())
    var isLoadingItems by mutableStateOf(false)
    
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    
    private val profileId get() = sessionManager.profileId

    fun loadAllData() {
        val currentProfileId = sessionManager.profileId
        if (currentProfileId == -1) {
            error = "No se encontró el ID de perfil. Por favor, inicie sesión de nuevo."
            return
        }
        
        viewModelScope.launch {
            // Only block the UI with a spinner on the first load. Later refreshes (tab switches,
            // post-mutation reloads) happen silently so the screen doesn't flash.
            isLoading = profile == null
            error = null
            try {
                // Fetch basic token from session if not set in NetworkModule
                if (NetworkModule.getAuthToken() == null) {
                    val token = sessionManager.token
                    if (!token.isNullOrEmpty()) {
                        NetworkModule.setAuthToken(token)
                    } else {
                        error = "Sesión expirada"
                        isLoading = false
                        return@launch
                    }
                }

                val profileRes = NetworkModule.organizerApi.getProfile(currentProfileId)
                if (profileRes.isSuccessful) {
                    profile = profileRes.body()
                } else {
                    error = "Error al obtener perfil: ${profileRes.code()}"
                }
                
                val catalogsRes = NetworkModule.organizerApi.getServiceCatalogs(currentProfileId)
                if (catalogsRes.isSuccessful) serviceCatalogs = catalogsRes.body() ?: emptyList()
                
                val albumsRes = NetworkModule.organizerApi.getAlbums(currentProfileId)
                if (albumsRes.isSuccessful) albums = albumsRes.body() ?: emptyList()
                
                val quotesRes = NetworkModule.organizerApi.getOrganizerQuotes(currentProfileId)
                if (quotesRes.isSuccessful) quotes = quotesRes.body() ?: emptyList()

                val socialEventsRes = NetworkModule.organizerApi.getSocialEvents()
                if (socialEventsRes.isSuccessful) {
                    socialEvents = socialEventsRes.body() ?: emptyList()
                }
                
                val reviewsRes = NetworkModule.organizerApi.getProfileReviews(currentProfileId)
                if (reviewsRes.isSuccessful) reviews = reviewsRes.body() ?: emptyList()
                
            } catch (e: Exception) {
                error = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Profile CRUD
    fun createProfile(profile: Profile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val res = NetworkModule.organizerApi.createProfile(profile)
                if (res.isSuccessful && res.body() != null) {
                    sessionManager.profileId = res.body()!!.id
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al crear perfil: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun updateProfile(profile: Profile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val res = NetworkModule.organizerApi.updateProfile(profile.id, profile)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al actualizar perfil: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadProfileImage(filePart: MultipartBody.Part, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.uploadProfileImage(sessionManager.profileId, filePart)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al subir imagen de perfil: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    // Service image upload (returns the Cloudinary secureUrl for use as ServiceCatalog.imageUrl)
    fun uploadServiceImage(filePart: MultipartBody.Part, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.uploadServiceCatalogImage(sessionManager.profileId, filePart)
                if (res.isSuccessful && res.body() != null) {
                    onSuccess(res.body()!!.secureUrl)
                } else {
                    error = "Error al subir imagen del servicio: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    // Service CRUD
    fun createService(catalog: ServiceCatalog, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.createServiceCatalog(sessionManager.profileId, catalog)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al crear servicio: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    fun updateService(catalogId: Int, catalog: ServiceCatalog, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.updateServiceCatalog(sessionManager.profileId, catalogId, catalog)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al actualizar servicio: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    fun deleteService(catalogId: Int) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.deleteServiceCatalog(sessionManager.profileId, catalogId)
                if (res.isSuccessful) {
                    loadAllData()
                } else {
                    error = "Error al eliminar servicio: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    // Albums & Images
    fun uploadAlbumImage(filePart: MultipartBody.Part, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.uploadAlbumImage(sessionManager.profileId, filePart)
                if (res.isSuccessful && res.body() != null) {
                    onSuccess(res.body()!!.secureUrl)
                } else {
                    error = "Error al subir imagen: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    fun createAlbum(album: Album, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.createAlbum(sessionManager.profileId, album)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al crear álbum: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    fun updateAlbum(albumId: Int, album: Album, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.updateAlbum(sessionManager.profileId, albumId, album)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al actualizar álbum: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    fun deleteAlbum(albumId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.deleteAlbum(sessionManager.profileId, albumId)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al eliminar álbum: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            }
        }
    }

    // Quote & Event creation (organizer prepares offers / registers events)
    fun createQuote(
        title: String,
        eventType: String,
        guestQuantity: Int,
        location: String,
        totalPrice: Double,
        eventDate: String,
        hostId: Int,
        onSuccess: () -> Unit
    ) {
        val organizerId = sessionManager.profileId
        if (organizerId == -1) { error = "Perfil no resuelto"; return }
        viewModelScope.launch {
            try {
                val body = CreateQuoteRequest(
                    title = title,
                    eventType = eventType,
                    guestQuantity = guestQuantity,
                    location = location,
                    totalPrice = totalPrice,
                    state = "PENDING",
                    eventDate = eventDate,
                    organizerId = organizerId,
                    hostId = hostId
                )
                val res = NetworkModule.organizerApi.createQuote(body)
                if (res.isSuccessful) {
                    LocalStore.pushNotification("Cotización creada", "\"$title\" fue registrada.", NotificationType.QUOTE)
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al crear cotización: ${res.code()}"
                }
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun createEvent(
        title: String,
        place: String,
        date: String,
        customerName: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val body = CreateSocialEventRequest(
                    title = title,
                    place = place,
                    date = date,
                    customerName = customerName,
                    status = "Active"
                )
                val res = NetworkModule.organizerApi.createSocialEvent(body)
                if (res.isSuccessful) {
                    LocalStore.pushNotification("Evento creado", "\"$title\" fue registrado.", NotificationType.SYSTEM)
                    loadAllData()
                    onSuccess()
                } else {
                    error = "Error al crear evento: ${res.code()}"
                }
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun deleteQuote(quoteId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.deleteQuote(quoteId)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else error = "Error al eliminar cotización: ${res.code()}"
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun deleteEvent(eventId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.deleteSocialEvent(eventId)
                if (res.isSuccessful) {
                    loadAllData()
                    onSuccess()
                } else error = "Error al eliminar evento: ${res.code()}"
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    // Quote & ServiceItem Management
    fun confirmQuote(quoteId: String) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.confirmQuote(quoteId)
                if (res.isSuccessful) {
                    val title = quotes.firstOrNull { it.id == quoteId }?.title ?: "Cotización"
                    LocalStore.pushNotification("Cotización aceptada", "\"$title\" fue marcada como aceptada.", NotificationType.QUOTE)
                    loadAllData()
                } else error = "Error al confirmar: ${res.code()}"
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun rejectQuote(quoteId: String) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.rejectQuote(quoteId)
                if (res.isSuccessful) {
                    val title = quotes.firstOrNull { it.id == quoteId }?.title ?: "Cotización"
                    LocalStore.pushNotification("Cotización rechazada", "\"$title\" fue marcada como rechazada.", NotificationType.QUOTE)
                    loadAllData()
                } else error = "Error al rechazar: ${res.code()}"
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun loadQuoteItems(quoteId: String) {
        viewModelScope.launch {
            isLoadingItems = true
            try {
                val res = NetworkModule.organizerApi.getQuoteServiceItems(quoteId)
                if (res.isSuccessful) {
                    currentQuoteItems = res.body() ?: emptyList()
                } else {
                    error = "Error al cargar ítems: ${res.code()}"
                }
            } catch (e: Exception) {
                error = e.localizedMessage
            } finally {
                isLoadingItems = false
            }
        }
    }

    fun addServiceItem(quoteId: String, item: ServiceItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.createServiceItem(quoteId, item)
                if (res.isSuccessful) {
                    loadQuoteItems(quoteId)
                    onSuccess()
                } else {
                    error = "Error al añadir ítem: ${res.code()}"
                }
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun updateServiceItem(quoteId: String, itemId: String, item: ServiceItem, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.updateServiceItem(quoteId, itemId, item)
                if (res.isSuccessful) {
                    loadQuoteItems(quoteId)
                    onSuccess()
                } else {
                    error = "Error al actualizar ítem: ${res.code()}"
                }
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }

    fun removeServiceItem(quoteId: String, itemId: String) {
        viewModelScope.launch {
            try {
                val res = NetworkModule.organizerApi.deleteServiceItem(quoteId, itemId)
                if (res.isSuccessful) loadQuoteItems(quoteId)
                else error = "Error al eliminar ítem: ${res.code()}"
            } catch (e: Exception) { error = e.localizedMessage }
        }
    }
}
