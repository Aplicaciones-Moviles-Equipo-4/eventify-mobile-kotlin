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
    
    var username by mutableStateOf("organizador_vip")
    var password by mutableStateOf("prueba123")
    
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
                val response = NetworkModule.authApi.signIn(SignInRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    sessionManager.token = token
                    NetworkModule.setAuthToken(token)
                    
                    // Recommended flow in Backend Report (6. Flujo 1):
                    // Resolve profile by email for demo or any other user.
                    // For seed data, organizador_vip maps to contacto@eventoselegantes.com
                    val emailToSearch = if (username == "organizador_vip") "contacto@eventoselegantes.com" else username // Try username as email for new users
                    
                    val profileRes = NetworkModule.organizerApi.getProfileByEmail(emailToSearch)
                    if (profileRes.isSuccessful && profileRes.body() != null) {
                        sessionManager.profileId = profileRes.body()!!.id
                    } else {
                        // Profile doesn't exist yet, we'll need to create it in CreateProfileScreen
                        sessionManager.profileId = -1
                    }
                    
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
                    // After successful signup, we might want to auto-login or just set success
                    // For now, let's just mark success to navigate to profile creation
                    registerSuccess = true
                } else {
                    error = "Error al registrar: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                error = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        NetworkModule.setAuthToken(null)
    }
}
