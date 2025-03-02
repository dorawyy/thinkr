package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMetadata(
    val source: String,
    val topic: String,
    val documentId: String? = null
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val timestamp: String
)

@Serializable
data class ChatSession(
    val sessionId: String,
    val userId: String,
    val messages: List<ChatMessage>,
    val createdAt: String,
    val updatedAt: String,
    val metadata: ChatMetadata
)

@Serializable
data class CreateSessionRequest(
    val userId: String,
    val metadata: ChatMetadata
)

@Serializable
data class SendMessageRequest(
    val message: String
)

@Serializable
data class ChatSessionResponse(
    val data: SessionData
)

@Serializable
data class SessionData(
    val session: ChatSession
)

@Serializable
data class MessageResponse(
    val data: MessageData
)

@Serializable
data class MessageData(
    val response: String
)

@Serializable
data class DeleteSessionResponse(
    val message: String
)
