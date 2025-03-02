package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.remote.RemoteApi

class SubscriptionRepository(private val remoteApi: RemoteApi) : ISubscriptionRepository {
    override suspend fun subscribe(userId: String): Result<SubscriptionResponse> {
        return try {
            val response = remoteApi.subscribe(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse> {
        return try {
            val response = remoteApi.getSubscriptionStatus(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
