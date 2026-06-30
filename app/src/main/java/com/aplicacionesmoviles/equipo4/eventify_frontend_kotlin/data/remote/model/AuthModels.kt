package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model

data class SignInRequest(
    val username: String,
    val password: String
)

data class SignInResponse(
    val id: Int,
    val username: String,
    val token: String
)

data class SignUpRequest(
    val username: String,
    val password: String,
    val roles: List<String>
)

data class SignUpResponse(
    val id: Int,
    val username: String,
    val roles: List<String>
)
