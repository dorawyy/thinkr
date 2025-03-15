package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.models.SubscriptionResponse
import com.example.thinkr.data.remote.subscription.SubscriptionApi
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class SubscriptionRepository(
    private val subscriptionApi: SubscriptionApi
) : ISubscriptionRepository {
    override suspend fun subscribe(userId: String): Result<SubscriptionResponse> {
        return try {
            val response = subscriptionApi.subscribe(userId)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: ResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    override suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse> {
        return try {
            val response = subscriptionApi.getSubscriptionStatus(userId)
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: ResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
