package com.example.thinkr.data.repositories.auth

import com.example.thinkr.data.remote.RemoteApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val remoteApi: RemoteApi) : IAuthRepository {
    override suspend fun login(
        googleId: String,
        name: String,
        email: String
    ) = withContext(Dispatchers.IO) {
        try {
            Result.success(remoteApi.login(googleId, name, email))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
