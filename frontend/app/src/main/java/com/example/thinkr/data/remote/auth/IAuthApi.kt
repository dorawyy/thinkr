package com.example.thinkr.data.remote.auth

import com.example.thinkr.data.models.AuthResponse

interface IAuthApi {
    suspend fun login(userId: String, name: String, email: String): AuthResponse
}
