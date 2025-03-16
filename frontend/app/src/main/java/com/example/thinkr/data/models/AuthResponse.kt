package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing authentication data.
 *
 * Wraps the authentication data returned from the API after a successful login or authentication request.
 *
 * @property data The contained authentication data.
 */
@Serializable
data class AuthResponse(
    val data: AuthData
)

/**
 * Container for user authentication information.
 *
 * Holds the authenticated user details returned from the server.
 *
 * @property user The authenticated user data.
 */
@Serializable
data class AuthData(
    val user: User
)
