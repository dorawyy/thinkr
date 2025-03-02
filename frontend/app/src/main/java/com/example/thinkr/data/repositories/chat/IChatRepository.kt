package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatMetadata
import com.example.thinkr.data.models.ChatSession

interface IChatRepository {
    suspend fun createChatSession(userId: String, metadata: ChatMetadata): Result<ChatSession>
    suspend fun sendMessage(sessionId: String, message: String): Result<String>
    suspend fun getChatSession(sessionId: String): Result<ChatSession>
    suspend fun deleteChatSession(sessionId: String): Result<String>
}
