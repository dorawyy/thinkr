package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage

/**
 * Interface for managing chat communication operations.
 *
 * Defines the contract for retrieving chat history, sending messages,
 * and clearing chat history with a remote API service.
 */
interface IChatRepository {
    suspend fun getChatHistory(userId: String): Result<ChatData>
    suspend fun sendMessage(userId: String, message: String): Result<ChatMessage>
    suspend fun clearChatHistory(userId: String): Result<String>
}
