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
    /**
     * Retrieves the chat history for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose chat history to retrieve.
     * @return Result containing ChatData on success or the appropriate exception on failure.
     */
    suspend fun getChatHistory(userId: String): Result<ChatData>

    /**
     * Sends a message from a user to the chat service and returns the response.
     *
     * @param userId The unique identifier of the user sending the message.
     * @param message The text content of the message to send.
     * @return Result containing the ChatMessage response on success or the appropriate exception on failure.
     */
    suspend fun sendMessage(userId: String, message: String): Result<ChatMessage>

    /**
     * Clears the chat history for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose chat history to clear.
     * @return Result containing a confirmation message on success or the appropriate exception on failure.
     */
    suspend fun clearChatHistory(userId: String): Result<String>
}
