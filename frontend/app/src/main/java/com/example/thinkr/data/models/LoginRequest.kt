package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val googleId: String,
    val name: String,
    val email: String
)
