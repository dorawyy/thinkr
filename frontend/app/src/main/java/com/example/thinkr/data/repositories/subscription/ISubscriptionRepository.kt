package com.example.thinkr.data.repositories.subscription

import com.example.thinkr.data.models.SubscriptionResponse

interface ISubscriptionRepository {
    suspend fun subscribe(userId: String): Result<SubscriptionResponse>
    suspend fun getSubscriptionStatus(userId: String): Result<SubscriptionResponse>
}
