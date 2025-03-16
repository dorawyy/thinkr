package com.example.thinkr.data.remote.auth

import com.example.thinkr.data.models.AuthResponse

/**
 * Interface for authentication-related API operations.
 *
 * Defines the contract for user authentication through remote API endpoints.
 */
interface IAuthApi {
    suspend fun login(userId: String, name: String, email: String): AuthResponse
}
