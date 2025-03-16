package com.example.thinkr.data.remote.chat

import com.example.thinkr.data.models.ChatHistoryResponse
import com.example.thinkr.data.models.DeleteChatHistoryResponse
import com.example.thinkr.data.models.SendChatMessageRequest
import com.example.thinkr.data.models.SendChatMessageResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/**
 * Implementation of the chat API interface.
 *
 * Handles network requests related to chat operations including
 * retrieving chat history, sending messages, and deleting chat history.
 *
 * @property client The HTTP client used to make network requests.
 */
class ChatApi(private val client: HttpClient) : IChatApi {
    /**
     * Retrieves chat history for a specific user.
     *
     * Makes a GET request to the chat history endpoint with the user's ID as a parameter.
     *
     * @param userId The unique identifier of the user whose chat history to retrieve.
     * @return ChatHistoryResponse containing the user's message history.
     */
    override suspend fun getChatHistory(userId: String): ChatHistoryResponse {
        val response = client.get(urlString = BASE_URL + CHAT) {
            parameter("userId", userId)
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    /**
     * Sends a new chat message for a user.
     *
     * Makes a POST request to the chat message endpoint with the user's ID and message content.
     *
     * @param userId The unique identifier of the user sending the message.
     * @param message The content of the message to send.
     * @return SendChatMessageResponse containing the result of the message operation.
     */
    override suspend fun sendChatMessage(userId: String, message: String): SendChatMessageResponse {
        val response = client.post(urlString = BASE_URL + CHAT + MESSAGE) {
            contentType(ContentType.Application.Json)
            setBody(SendChatMessageRequest(userId, message))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    /**
     * Deletes the entire chat history for a user.
     *
     * Makes a DELETE request to the chat history endpoint with the user's ID as a parameter.
     *
     * @param userId The unique identifier of the user whose chat history to delete.
     * @return DeleteChatHistoryResponse containing the result of the deletion operation.
     */
    override suspend fun deleteChatHistory(userId: String): DeleteChatHistoryResponse {
        val response = client.delete(urlString = BASE_URL + CHAT + HISTORY) {
            parameter("userId", userId)
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val CHAT = "/chat"
        private const val MESSAGE = "/message"
        private const val HISTORY = "/history"
    }
}
