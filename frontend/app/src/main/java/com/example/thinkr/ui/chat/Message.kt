package com.example.thinkr.ui.chat

import androidx.compose.runtime.State
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Represents a chat message in the application.
 *
 * This data class models a single message in a chat conversation, containing
 * the message content, metadata, and information about the sender.
 *
 * @property id Unique identifier for the message.
 * @property content The text content of the message as a State object to support streaming display.
 * @property timestamp The time when the message was sent, as milliseconds since epoch.
 * @property isSender Boolean flag indicating whether the current user is the sender (true)
 *                   or if it's a response from the AI (false).
 */
data class Message(
    val id: String,
    val content: State<String>,
    val timestamp: Long,
    val isSender: Boolean
)

/**
 * Extension property that formats the message timestamp into a human-readable time string.
 *
 * Converts the timestamp into a formatted string using the pattern "h:mm a"
 * (e.g., "3:45 PM") based on the device's default locale.
 */
val Message.formattedTime: String
    get() {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
