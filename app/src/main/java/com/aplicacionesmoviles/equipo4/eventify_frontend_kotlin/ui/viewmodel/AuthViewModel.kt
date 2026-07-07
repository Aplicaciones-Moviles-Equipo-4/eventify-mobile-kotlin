package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.local.SessionManager
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.NetworkModule
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignInRequest
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignUpRequest
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)
    
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)
    var registerSuccess by mutableStateOf(false)
    
    fun onUsernameChange(newValue: String) { username = newValue }
    fun onPasswordChange(newValue: String) { password = newValue }
    
    fun signIn() {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val authenticated = authenticate(username, password)
                if (authenticated) {
                    resolveProfileId(username)
                    loginSuccess = true
                } else {
                    error = "Credenciales incorrectas"
                }
            } catch (e: Exception) {
                error = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun signUp(usernameRegister: String, passwordRegister: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                // Backend expects a list of roles, defaulting to ROLE_USER as per report
                val request = SignUpRequest(usernameRegister, passwordRegister, listOf("ROLE_USER"))
                val response = NetworkModule.authApi.signUp(request)

                if (response.isSuccessful) {
                    // Auto sign-in right after registering so the following profile-creation
                    // call is authenticated (POST /profiles requires a valid JWT).
                    val authenticated = authenticate(usernameRegister, passwordRegister)
                    if (authenticated) {
                        // Brand-new user has no profile yet -> CreateProfileScreen will create it.
                        sessionManager.profileId = -1
                        registerSuccess = true
                    } else {
                        error = "Cuenta creada, pero no se pudo iniciar sesión. Intenta ingresar manualmente."
                    }
                } else {
                    // Surface the backend message (e.g. "Username already exists") instead of a raw code.
                    val serverMessage = extractServerMessage(response.errorBody()?.string())
                    error = when {
                        serverMessage?.contains("already exists", ignoreCase = true) == true ->
                            "Ese nombre de usuario ya está registrado. Prueba con otro."
                        serverMessage != null -> serverMessage
                        else -> "No se pudo registrar (código ${response.code()}). Intenta con otro usuario."
                    }
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    /** Performs the sign-in call and persists the token. Returns true on success. */
    private suspend fun authenticate(user: String, pass: String): Boolean {
        val response = NetworkModule.authApi.signIn(SignInRequest(user, pass))
        val body = response.body()
        if (response.isSuccessful && body != null) {
            sessionManager.token = body.token
            // Persist the login identifier. For real users this is their email, later reused
            // as the Profile email so the profileId can be resolved on subsequent logins.
            sessionManager.accountEmail = user
            NetworkModule.setAuthToken(body.token)
            return true
        }
        return false
    }

    /**
     * Resolves the business profileId. There is no /me/profile endpoint, so we look it up by
     * email (Backend Report, Flujo 1/2). The demo organizer maps to a known seed email.
     */
    private suspend fun resolveProfileId(user: String) {
        val emailToSearch =
            if (user == "organizador_vip") "contacto@eventoselegantes.com" else user
        try {
            val profileRes = NetworkModule.organizerApi.getProfileByEmail(emailToSearch)
            val profile = profileRes.body()
            sessionManager.profileId =
                if (profileRes.isSuccessful && profile != null) profile.id else -1
        } catch (e: Exception) {
            sessionManager.profileId = -1
        }
    }

    /** Extracts the "message" field from a backend JSON error body like {"message":"..."}. */
    private fun extractServerMessage(body: String?): String? {
        if (body.isNullOrBlank()) return null
        val marker = "\"message\""
        val idx = body.indexOf(marker)
        if (idx == -1) return null
        val colon = body.indexOf(':', idx + marker.length)
        if (colon == -1) return null
        val firstQuote = body.indexOf('"', colon + 1)
        if (firstQuote == -1) return null
        val secondQuote = body.indexOf('"', firstQuote + 1)
        if (secondQuote == -1) return null
        return body.substring(firstQuote + 1, secondQuote).ifBlank { null }
    }

    fun logout() {
        sessionManager.logout()
        NetworkModule.setAuthToken(null)
    }
}
