package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.api

import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignInRequest
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignInResponse
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignUpRequest
import com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>
}
