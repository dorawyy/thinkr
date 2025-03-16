package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.remote.auth.AuthApi
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Implementation of the authentication repository interface.
 *
 * Handles user authentication operations through a remote API service.
 * Wraps network responses in Result objects to handle success and failure cases.
 *
 * @property authApi The API service used to make authentication-related network requests.
 */
class AuthRepository(private val authApi: AuthApi) : IAuthRepository {
    /**
     * Authenticates a user using Google credentials.
     *
     * Executes the network request on the IO dispatcher and handles potential exceptions.
     *
     * @param googleId The unique identifier from Google authentication.
     * @param name The user's display name.
     * @param email The user's email address.
     * @return Result containing AuthResponse on success or the appropriate exception on failure.
     */
    override suspend fun login(
        googleId: String,
        name: String,
        email: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            Result.success(authApi.login(googleId, name, email))
        } catch (e: IOException) {
            e.printStackTrace()
            Result.failure(e)
        } catch (e: ResponseException) {
            e.printStackTrace()
            Result.failure(e)
        } catch (e: SerializationException) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
