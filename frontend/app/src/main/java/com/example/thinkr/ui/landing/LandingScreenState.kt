package com.example.thinkr.ui.landing

/**
 * Data class representing the UI state of the landing screen.
 *
 * @property isLoading Boolean indicating whether authentication is in progress.
 * @property error String containing any error message to display during sign-in process, or null if no error.
 * @property isAuthenticated Boolean indicating whether the user has successfully authenticated.
 */
data class LandingScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)
