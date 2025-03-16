package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Data model representing a user login request.
 *
 * Contains the essential information needed to authenticate a user with the server.
 * This class is serializable for network transmission.
 *
 * @property googleId The unique identifier associated with the user's Google account.
 * @property name The display name of the user.
 * @property email The email address of the user.
 */
@Serializable
data class LoginRequest(
    val googleId: String,
    val name: String,
    val email: String
)
