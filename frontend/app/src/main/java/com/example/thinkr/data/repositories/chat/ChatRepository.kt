package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage
import com.example.thinkr.data.remote.RemoteApi

class ChatRepository(private val remoteApi: RemoteApi) : IChatRepository {
    override suspend fun getChatHistory(userId: String): Result<ChatData> {
        return try {
            val response = remoteApi.getChatHistory(userId)
            Result.success(response.data.chat)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(userId: String, message: String): Result<ChatMessage> {
        return try {
            val response = remoteApi.sendChatMessage(userId, message)
            Result.success(response.data.response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearChatHistory(userId: String): Result<String> {
        return try {
            val response = remoteApi.deleteChatHistory(userId)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
