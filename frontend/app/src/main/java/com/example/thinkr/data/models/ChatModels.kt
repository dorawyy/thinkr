package com.example.thinkr.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatHistoryResponse(
    val data: ChatHistoryData
)

@Serializable
data class ChatHistoryData(
    val chat: ChatData
)

@Serializable
data class ChatData(
    val userId: String,
    val messages: List<ChatMessage>,
    val createdAt: String,
    val updatedAt: String,
    val metadata: Map<String, String>
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: String,
    @SerialName("_id") val id: String? = null
)

@Serializable
data class SendChatMessageRequest(
    val userId: String,
    val message: String
)

@Serializable
data class SendChatMessageResponse(
    val data: ChatMessageResponseData
)

@Serializable
data class ChatMessageResponseData(
    val response: ChatMessage
)

@Serializable
data class DeleteChatHistoryResponse(
    val message: String
)
