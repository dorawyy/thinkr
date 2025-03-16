package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.models.AuthResponse

/**
 * Interface for managing authentication operations.
 *
 * Defines the contract for user login and authentication with the remote API service.
 */
interface IAuthRepository {
    /**
     * Authenticates a user using Google credentials.
     *
     * @param googleId The unique identifier from Google authentication.
     * @param name The user's display name.
     * @param email The user's email address.
     * @return Result containing AuthResponse on success or the appropriate exception on failure.
     */
    suspend fun login(
        googleId: String,
        name: String,
        email: String
    ): Result<AuthResponse>
}
