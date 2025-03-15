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

class ChatApi(private val client: HttpClient) : IChatApi {
    override suspend fun getChatHistory(userId: String): ChatHistoryResponse {
        val response = client.get(urlString = BASE_URL + CHAT) {
            parameter("userId", userId)
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun sendChatMessage(userId: String, message: String): SendChatMessageResponse {
        val response = client.post(urlString = BASE_URL + CHAT + MESSAGE) {
            contentType(ContentType.Application.Json)
            setBody(SendChatMessageRequest(userId, message))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

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
