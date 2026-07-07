package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote

import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api.AuthApi
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api.OrganizerApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "https://eventify-platform.onrender.com/api/v1/"
    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    fun getAuthToken(): String? = authToken

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        
        authToken?.let {
            requestBuilder.header("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        // Generous read timeout because the backend runs on a free tier that can cold-start
        // (~30-50s) on the first request; without timeouts a stalled request would hang forever.
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val organizerApi: OrganizerApi = retrofit.create(OrganizerApi::class.java)
}
