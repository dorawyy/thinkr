package com.example.thinkr.ui.payment

/**
 * Data class representing the UI state of the payment screen.
 *
 * @property isSubscribed Boolean indicating whether the user has successfully subscribed.
 * @property errorMessage String containing any error message to display during payment process.
 */
data class PaymentScreenState(
    val isSubscribed: Boolean = false,
    val errorMessage: String = ""
)
