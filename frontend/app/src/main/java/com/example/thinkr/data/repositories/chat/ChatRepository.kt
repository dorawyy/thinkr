package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage
import com.example.thinkr.data.remote.chat.ChatApi
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Implementation of the chat repository interface.
 *
 * Handles chat-related network operations including retrieving chat history,
 * sending messages, and clearing chat history. Wraps network responses in
 * Result objects to handle success and failure cases.
 *
 * @property chatApi The API service used to make chat-related network requests.
 */
class ChatRepository(private val chatApi: ChatApi) : IChatRepository {
    /**
     * Retrieves the chat history for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose chat history to retrieve.
     * @return Result containing ChatData on success or the appropriate exception on failure.
     */
    override suspend fun getChatHistory(userId: String): Result<ChatData> {
        return try {
            val response = chatApi.getChatHistory(userId)
            Result.success(response.data.chat)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: ResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    /**
     * Sends a message from a user to the chat service and returns the response.
     *
     * @param userId The unique identifier of the user sending the message.
     * @param message The text content of the message to send.
     * @return Result containing the ChatMessage response on success or the appropriate exception on failure.
     */
    override suspend fun sendMessage(userId: String, message: String): Result<ChatMessage> {
        return try {
            val response = chatApi.sendChatMessage(userId, message)
            Result.success(response.data.response)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: ResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }

    /**
     * Clears the chat history for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose chat history to clear.
     * @return Result containing a confirmation message on success or the appropriate exception on failure.
     */
    override suspend fun clearChatHistory(userId: String): Result<String> {
        return try {
            val response = chatApi.deleteChatHistory(userId)
            Result.success(response.message)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: ResponseException) {
            Result.failure(e)
        } catch (e: SerializationException) {
            Result.failure(e)
        }
    }
}
