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
    /**
     * Retrieves chat history for a specific user.
     *
     * @param userId The unique identifier of the user whose chat history to retrieve.
     * @return ChatHistoryResponse containing the user's message history.
     */
    suspend fun getChatHistory(userId: String): ChatHistoryResponse

    /**
     * Sends a new chat message for a user.
     *
     * @param userId The unique identifier of the user sending the message.
     * @param message The content of the message to send.
     * @return SendChatMessageResponse containing the result of the message operation.
     */
    suspend fun sendChatMessage(userId: String, message: String): SendChatMessageResponse

    /**
     * Deletes the entire chat history for a user.
     *
     * @param userId The unique identifier of the user whose chat history to delete.
     * @return DeleteChatHistoryResponse containing the result of the deletion operation.
     */
    suspend fun deleteChatHistory(userId: String): DeleteChatHistoryResponse
}
