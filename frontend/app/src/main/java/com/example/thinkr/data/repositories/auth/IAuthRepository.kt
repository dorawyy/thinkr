package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.models.AuthResponse

/**
 * Interface for managing authentication operations.
 *
 * Defines the contract for user login and authentication with the remote API service.
 */
interface IAuthRepository {
    suspend fun login(
        googleId: String,
        name: String,
        email: String
    ): Result<AuthResponse>
}
