package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatMetadata
import com.example.thinkr.data.models.ChatSession
import com.example.thinkr.data.remote.RemoteApi

class ChatRepository(private val remoteApi: RemoteApi) : IChatRepository {
    override suspend fun createChatSession(
        userId: String,
        metadata: ChatMetadata
    ): Result<ChatSession> {
        return try {
            val response = remoteApi.createChatSession(userId, metadata)
            Result.success(response.data.session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(
        sessionId: String,
        message: String
    ): Result<String> {
        return try {
            val response = remoteApi.sendMessage(sessionId, message)
            Result.success(response.data.response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatSession(sessionId: String): Result<ChatSession> {
        return try {
            val response = remoteApi.getChatSession(sessionId)
            Result.success(response.data.session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteChatSession(sessionId: String): Result<String> {
        return try {
            val response = remoteApi.deleteChatSession(sessionId)
            Result.success(response.message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
