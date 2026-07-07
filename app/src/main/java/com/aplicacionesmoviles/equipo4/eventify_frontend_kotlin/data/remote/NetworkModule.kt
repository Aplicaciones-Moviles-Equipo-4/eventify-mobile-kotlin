package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote

import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api.AuthApi
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api.OrganizerApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Módulo de red que centraliza la configuración de Retrofit y OkHttp.
 * Proporciona acceso a las interfaces de la API y gestiona el token de autenticación.
 */
object NetworkModule {
    /** URL base de la plataforma para las peticiones a la API. */
    private const val BASE_URL = "https://eventify-platform.onrender.com/api/v1/"
    
    /** Almacenamiento volátil del token de autenticación actual. */
    private var authToken: String? = null

    /**
     * Establece el token de autenticación global para las peticiones.
     * 
     * @param token El token JWT obtenido tras la autenticación exitosa.
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }

    /**
     * Obtiene el token de autenticación configurado actualmente.
     * 
     * @return El token en formato String, o nulo si no ha sido establecido.
     */
    fun getAuthToken(): String? = authToken

    /** 
     * Interceptor para registrar el cuerpo de las peticiones y respuestas en logcat.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Interceptor personalizado para inyectar el token de autorización en el encabezado
     * de cada petición saliente.
     */
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        
        authToken?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }

    /**
     * Cliente HTTP configurado con tiempos de espera personalizados e interceptores.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        // Generous read timeout because the backend runs on a free tier that can cold-start
        // (~30-50s) on the first request; without timeouts a stalled request would hang forever.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Configuración de la instancia de Retrofit con Gson y el cliente personalizado.
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    /** 
     * Instancia del servicio de autenticación.
     */
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    
    /** 
     * Instancia del servicio para datos del organizador.
     */
    val organizerApi: OrganizerApi = retrofit.create(OrganizerApi::class.java)
}
