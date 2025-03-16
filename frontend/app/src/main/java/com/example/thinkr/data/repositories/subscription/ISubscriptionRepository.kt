package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.models.SubscriptionResponse

/**
 * Interface for managing user subscription operations.
 *
 * Defines the contract for subscribing users and retrieving subscription status
 * through remote API calls.
 */
interface ISubscriptionRepository {
    /**
     * Subscribes a user to premium features by making a network request.
     *
     * @param userId The unique identifier of the user to subscribe.
     * @return Result containing subscription response on success or the appropriate exception on failure.
     */
    suspend fun subscribe(userId: String): Result<SubscriptionResponse>

    /**
     * Retrieves the current subscription status for a user from the remote API.
     *
     * @param userId The unique identifier of the user whose subscription status to check.
     * @return Result containing subscription response on success or the appropriate exception on failure.
     */
    suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse>
}
