package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Data model representing a user in the Thinkr application.
 *
 * Stores essential information about a user including their personal details and subscription status.
 * This class is serializable for network transmission and storage purposes.
 *
 * @property email The user's email address.
 * @property name The user's display name.
 * @property googleId The unique identifier associated with the user's Google account.
 * @property subscribed Boolean indicating whether the user has an active subscription.
 */
@Serializable
data class User(
    val email: String,
    val name: String,
    val googleId: String,
    val subscribed: Boolean
)
