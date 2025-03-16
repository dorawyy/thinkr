package com.example.thinkr.ui.profile

/**
 * Data class representing the UI state of the profile screen.
 *
 * @property username The display name of the current user.
 * @property email The email address of the current user.
 * @property isPremium Boolean indicating whether the user has premium subscription status.
 */
data class ProfileScreenState(
    val username: String = "",
    val email: String = "",
    val isPremium: Boolean = false
)
