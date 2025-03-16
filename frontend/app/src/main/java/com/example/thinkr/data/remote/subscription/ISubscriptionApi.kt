package com.example.thinkr.data.remote.subscription

import com.example.thinkr.data.models.SubscriptionResponse

/**
 * Interface for subscription-related API operations.
 *
 * Defines the contract for managing user subscriptions through remote API endpoints.
 */
interface ISubscriptionApi {
    /**
     * Subscribes a user to the premium service.
     *
     * @param userId The unique identifier of the user to subscribe.
     * @return SubscriptionResponse containing subscription details and status.
     */
    suspend fun subscribe(userId: String): SubscriptionResponse

    /**
     * Retrieves the current subscription status for a user.
     *
     * @param userId The unique identifier of the user whose subscription status to check.
     * @return SubscriptionResponse containing subscription details and status.
     */
    suspend fun getSubscriptionStatus(userId: String): SubscriptionResponse
}
