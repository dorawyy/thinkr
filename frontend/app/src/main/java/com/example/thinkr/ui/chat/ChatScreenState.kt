package com.example.thinkr.ui.chat

/**
 * Data class representing the UI state of the chat screen.
 *
 * Contains information about the current user, message history, loading state,
 * and any error messages that need to be displayed to the user.
 *
 * @property userId The unique identifier of the current user participating in the chat.
 * @property messages List of all messages in the current chat conversation.
 * @property isLoading Boolean flag indicating whether chat data is currently being loaded.
 * @property error Optional error message to display when a chat operation fails.
 */
data class ChatScreenState(
    val userId: String = "",
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
