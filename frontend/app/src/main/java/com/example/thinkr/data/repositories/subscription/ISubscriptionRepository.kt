package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.models.SubscriptionResponse

/**
 * Interface for managing user subscription operations.
 *
 * Defines the contract for subscribing users and retrieving subscription status
 * through remote API calls.
 */
interface ISubscriptionRepository {
    suspend fun subscribe(userId: String): Result<SubscriptionResponse>
    suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse>
}
