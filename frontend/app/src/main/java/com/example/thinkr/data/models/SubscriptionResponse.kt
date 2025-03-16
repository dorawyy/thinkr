package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing subscription information.
 *
 * Wraps the subscription data returned from the API.
 *
 * @property data The contained subscription data.
 */
@Serializable
data class SubscriptionResponse(
    val data: SubscriptionData
)

/**
 * Container for user subscription information.
 *
 * Holds details about a user's identity and subscription status.
 *
 * @property email The user's email address.
 * @property name The user's display name.
 * @property googleId The unique identifier associated with the user's Google account.
 * @property subscribed Boolean indicating whether the user has an active subscription.
 */
@Serializable
data class SubscriptionData(
    val email: String,
    val name: String,
    val googleId: String,
    val subscribed: Boolean
)
