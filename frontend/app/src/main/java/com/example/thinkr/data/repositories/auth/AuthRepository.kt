package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.remote.auth.AuthApi
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class AuthRepository(private val authApi: AuthApi) : IAuthRepository {
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
