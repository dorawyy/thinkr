package com.example.thinkr.data.remote.subscription

import com.example.thinkr.data.models.SubscriptionResponse

interface ISubscriptionApi {
    suspend fun subscribe(userId: String): SubscriptionResponse
    suspend fun getSubscriptionStatus(userId: String): SubscriptionResponse
}
