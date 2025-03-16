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

/**
 * Implementation of the authentication API interface.
 *
 * Handles network requests related to user authentication.
 *
 * @property client The HTTP client used to make network requests.
 */
class AuthApi(private val client: HttpClient) : IAuthApi {
    /**
     * Authenticates a user by logging them in.
     *
     * Makes a POST request to the login endpoint with the user credentials.
     *
     * @param userId The unique identifier of the user to authenticate.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @return AuthResponse containing authentication details and status.
     */
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
