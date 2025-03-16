package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.models.SubscriptionResponse
import com.example.thinkr.data.remote.subscription.SubscriptionApi
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Implementation of the subscription repository interface.
 *
 * Handles subscription-related API calls including subscription creation and status checks.
 * Wraps network responses in Result objects to handle success and failure cases.
 *
 * @property subscriptionApi The API service used to make subscription network requests.
 */
class SubscriptionRepository(
    private val subscriptionApi: SubscriptionApi
) : ISubscriptionRepository {
    /**
     * Subscribes a user to premium features by making a network request.
     *
     * @param userId The unique identifier of the user to subscribe.
     * @return Result containing subscription response on success or the appropriate exception on failure.
     */
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

    /**
     * Retrieves the current subscription status for a user from the remote API.
     *
     * @param userId The unique identifier of the user whose subscription status to check.
     * @return Result containing subscription response on success or the appropriate exception on failure.
     */
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
