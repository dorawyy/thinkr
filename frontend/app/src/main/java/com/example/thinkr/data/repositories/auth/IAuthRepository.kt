package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.models.AuthResponse

interface IAuthRepository {
    suspend fun login(
        googleId: String,
        name: String,
        email: String
    ): Result<AuthResponse>
}
