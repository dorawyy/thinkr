package com.example.thinkr.data.remote.chat

import com.example.thinkr.data.models.ChatHistoryResponse
import com.example.thinkr.data.models.DeleteChatHistoryResponse
import com.example.thinkr.data.models.SendChatMessageResponse

/**
 * Interface for chat-related API operations.
 *
 * Defines the contract for retrieving chat history, sending messages,
 * and deleting chat history through remote API endpoints.
 */
interface IChatApi {
    suspend fun getChatHistory(userId: String): ChatHistoryResponse
    suspend fun sendChatMessage(userId: String, message: String): SendChatMessageResponse
    suspend fun deleteChatHistory(userId: String): DeleteChatHistoryResponse
}
