package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing chat history data.
 *
 * Wraps the chat history data returned from the API.
 *
 * @property data The contained chat history data.
 */
@Serializable
data class ChatHistoryResponse(
    val data: ChatHistoryData
)

/**
 * Container for chat history information.
 *
 * Holds the chat data structure.
 *
 * @property chat The detailed chat data.
 */
@Serializable
data class ChatHistoryData(
    val chat: ChatData
)

/**
 * Detailed information about a chat session.
 *
 * Contains the messages, timestamps, and metadata for a chat.
 *
 * @property messages List of chat messages exchanged in this chat session.
 * @property createdAt Timestamp indicating when the chat was created.
 * @property updatedAt Timestamp indicating when the chat was last updated.
 * @property metadata Additional information about the chat as key-value pairs.
 */
@Serializable
data class ChatData(
    val messages: List<ChatMessage>,
    val createdAt: String,
    val updatedAt: String,
    val metadata: Map<String, String>
)

/**
 * Represents a single message within a chat.
 *
 * Contains the message content, sender role, and timestamp.
 *
 * @property role The role of the message sender (e.g., "user", "assistant").
 * @property content The text content of the message.
 * @property timestamp When the message was sent.
 */
@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: String
)

/**
 * Request model for sending a new chat message.
 *
 * Contains the necessary data to send a message to the chat API.
 *
 * @property userId The identifier of the user sending the message.
 * @property message The text content of the message being sent.
 */
@Serializable
data class SendChatMessageRequest(
    val userId: String,
    val message: String
)

/**
 * Response model after sending a chat message.
 *
 * Wraps the response data returned after a message is sent.
 *
 * @property data The contained response data.
 */
@Serializable
data class SendChatMessageResponse(
    val data: ChatMessageResponseData
)

/**
 * Container for the message response data.
 *
 * Holds the response message from the API.
 *
 * @property response The chat message returned in response.
 */
@Serializable
data class ChatMessageResponseData(
    val response: ChatMessage
)

/**
 * Response model for chat history deletion.
 *
 * Contains the server's confirmation message after deleting chat history.
 *
 * @property message Confirmation or status message about the deletion.
 */
@Serializable
data class DeleteChatHistoryResponse(
    val message: String
)
