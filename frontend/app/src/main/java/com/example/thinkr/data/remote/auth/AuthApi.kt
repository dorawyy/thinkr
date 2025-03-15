package com.example.thinkr.data.remote.auth

import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.models.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class AuthApi(private val client: HttpClient) : IAuthApi {
    override suspend fun login(userId: String, name: String, email: String): AuthResponse {
        val response = client.post(urlString = BASE_URL + AUTH + LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(userId, name, email))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val AUTH = "/auth"
        private const val LOGIN = "/login"
    }
}