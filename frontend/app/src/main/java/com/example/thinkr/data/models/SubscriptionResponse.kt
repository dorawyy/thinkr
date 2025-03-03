package com.example.thinkr.data.models

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
