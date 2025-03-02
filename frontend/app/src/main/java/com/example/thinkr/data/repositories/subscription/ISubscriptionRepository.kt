package com.example.thinkr.data.repositories.subscription

interface ISubscriptionRepository {
    suspend fun subscribe(userId: String): Result<SubscriptionResponse>
    suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse>
}
