package com.example.thinkr.data.repositories.subscription

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionResponse(
    val data: SubscriptionData
)

@Serializable
data class SubscriptionData(
    val email: String,
    val name: String,
    val googleId: String,
    val subscribed: Boolean
)
