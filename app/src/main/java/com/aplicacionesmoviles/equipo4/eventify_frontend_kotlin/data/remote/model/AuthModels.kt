package com.aplicacionesmoviles.equipo4.eventify_frontend_kotlin.data.remote.model

/**
 * Modelo que representa una solicitud de inicio de sesión.
 *
 * @property username Nombre de usuario para la autenticación.
 * @property password Contraseña del usuario.
 */
data class SignInRequest(
    val username: String,
    val password: String
)

/**
 * Modelo que representa la respuesta tras un inicio de sesión exitoso.
 *
 * @property id Identificador único del usuario.
 * @property username Nombre de usuario autenticado.
 * @property token Token de autenticación (JWT) para realizar peticiones protegidas.
 */
data class SignInResponse(
    val id: Int,
    val username: String,
    val token: String
)

/**
 * Modelo que representa una solicitud de registro de nuevo usuario.
 *
 * @property username Nombre de usuario deseado.
 * @property password Contraseña para la nueva cuenta.
 * @property roles Lista de roles asignados al usuario (ej. "ROLE_USER", "ROLE_ORGANIZER").
 */
data class SignUpRequest(
    val username: String,
    val password: String,
    val roles: List<String>
)

/**
 * Modelo que representa la respuesta tras un registro exitoso.
 *
 * @property id Identificador único asignado al nuevo usuario.
 * @property username Nombre de usuario registrado.
 * @property roles Lista de roles asignados al usuario.
 */
data class SignUpResponse(
    val id: Int,
    val username: String,
    val roles: List<String>
)
