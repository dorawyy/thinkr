package com.example.thinkr.data.repositories.chat

import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage

interface IChatRepository {
    suspend fun getChatHistory(userId: String): Result<ChatData>
    suspend fun sendMessage(userId: String, message: String): Result<ChatMessage>
    suspend fun clearChatHistory(userId: String): Result<String>
}
