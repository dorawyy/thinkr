package com.example.thinkr.data.remote.auth

import com.example.thinkr.data.models.AuthResponse

/**
 * Interface for authentication-related API operations.
 *
 * Defines the contract for user authentication through remote API endpoints.
 */
interface IAuthApi {
    /**
     * Authenticates a user by logging them in.
     *
     * @param userId The unique identifier of the user to authenticate.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @return AuthResponse containing authentication details and status.
     */
    suspend fun login(userId: String, name: String, email: String): AuthResponse
}
