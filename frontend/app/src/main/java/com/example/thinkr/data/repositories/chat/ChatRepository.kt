package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage
import com.example.thinkr.data.remote.chat.ChatApi
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class ChatRepository(private val chatApi: ChatApi) : IChatRepository {
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
