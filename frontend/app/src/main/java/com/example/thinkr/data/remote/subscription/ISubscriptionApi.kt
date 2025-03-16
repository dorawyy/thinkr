package com.example.thinkr.data.remote.subscription

import com.example.thinkr.data.models.SubscriptionResponse

/**
 * Interface for subscription-related API operations.
 *
 * Defines the contract for managing user subscriptions through remote API endpoints.
 */
interface ISubscriptionApi {
    suspend fun subscribe(userId: String): SubscriptionResponse
    suspend fun getSubscriptionStatus(userId: String): SubscriptionResponse
}
