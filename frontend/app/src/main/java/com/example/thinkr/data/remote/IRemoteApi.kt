package com.example.thinkr.data.remote

import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.models.ChatMetadata
import com.example.thinkr.data.models.ChatSessionResponse
import com.example.thinkr.data.models.DeleteSessionResponse
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.MessageResponse
import com.example.thinkr.data.models.UploadResponse
import com.example.thinkr.data.repositories.subscription.SubscriptionResponse
import java.io.InputStream

interface IRemoteApi {
    suspend fun login(
        userId: String,
        name: String,
        email: String
    ): AuthResponse

    suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document>

    suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): UploadResponse

    suspend fun subscribe(
        userId: String
    ): SubscriptionResponse

    suspend fun getSubscriptionStatus(
        userId: String
    ): SubscriptionResponse

    suspend fun createChatSession(
        userId: String,
        metadata: ChatMetadata
    ): ChatSessionResponse

    suspend fun sendMessage(
        sessionId: String,
        message: String
    ): MessageResponse

    suspend fun getChatSession(
        sessionId: String
    ): ChatSessionResponse

    suspend fun deleteChatSession(
        sessionId: String
    ): DeleteSessionResponse
}
