package com.example.thinkr.data.remote

import android.util.Log
import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.models.ChatMetadata
import com.example.thinkr.data.models.ChatSessionResponse
import com.example.thinkr.data.models.CreateSessionRequest
import com.example.thinkr.data.models.DeleteSessionResponse
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.FlashcardsResponse
import com.example.thinkr.data.models.LoginRequest
import com.example.thinkr.data.models.MessageResponse
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.models.QuizResponse
import com.example.thinkr.data.models.SendMessageRequest
import com.example.thinkr.data.models.UploadResponse
import com.example.thinkr.data.models.SubscriptionResponse
import com.example.thinkr.data.models.SuggestedMaterialsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class RemoteApi(private val client: HttpClient) : IRemoteApi {
    override suspend fun login(userId: String, name: String, email: String): AuthResponse {
        val response = client.post(urlString = BASE_URL + AUTH + LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(userId, name, email))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document> {
        val response = client.get(urlString = BASE_URL + DOCUMENT + RETRIEVE) {
            parameter("userId", userId)
            parameter(
                key = "documentIds",
                value = if (documentIds == null) "[]" else Json.encodeToString(
                    serializer = ListSerializer(String.serializer()),
                    value = documentIds
                )
            )
        }
        val responseBody = response.bodyAsText()
        val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject
        val docs = jsonResponse["data"]?.jsonObject?.get("docs")?.jsonArray
        return docs?.map { Json.decodeFromJsonElement(Document.serializer(), it) } ?: emptyList()
    }

    override suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): UploadResponse {
        val response = client.post(urlString = BASE_URL + DOCUMENT + UPLOAD) {
            contentType(ContentType.MultiPart.FormData)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("userId", userId)
                        append("documentName", documentName)
                        append("document", fileBytes, Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                            append(HttpHeaders.ContentType, "application/pdf")
                        })
                    }
                )
            )
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun subscribe(
        userId: String
    ): SubscriptionResponse {
        val response = client.post(urlString = BASE_URL + SUBSCRIPTION) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun getSubscriptionStatus(
        userId: String
    ): SubscriptionResponse {
        val response = client.get(urlString = BASE_URL + SUBSCRIPTION) {
            parameter("userId", userId)
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun createChatSession(
        userId: String,
        metadata: ChatMetadata
    ): ChatSessionResponse {
        val response = client.post(urlString = BASE_URL + CHAT) {
            contentType(ContentType.Application.Json)
            setBody(CreateSessionRequest(userId, metadata))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun sendMessage(
        sessionId: String,
        message: String
    ): MessageResponse {
        val response = client.post(urlString = "$BASE_URL$CHAT/$sessionId$MESSAGE") {
            contentType(ContentType.Application.Json)
            setBody(SendMessageRequest(message))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun getChatSession(sessionId: String): ChatSessionResponse {
        val response = client.get(urlString = "$BASE_URL$CHAT/$sessionId")
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun deleteChatSession(sessionId: String): DeleteSessionResponse {
        val response = client.delete(urlString = "$BASE_URL$CHAT/$sessionId")
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    override suspend fun getFlashcards(userId: String, documentId: String): List<FlashcardItem> {
        val response = client.get(urlString = BASE_URL + STUDY + FLASHCARDS) {
            parameter("userId", userId)
            parameter("documentId", documentId)
        }
        val responseBody = response.bodyAsText()
        val flashcardsResponse = Json.decodeFromString<FlashcardsResponse>(responseBody)
        return flashcardsResponse.data.flashcards
    }

    override suspend fun getQuiz(userId: String, documentId: String): List<QuizItem> {
        val response = client.get(urlString = BASE_URL + STUDY + QUIZ) {
            parameter("userId", userId)
            parameter("documentId", documentId)
        }
        val responseBody = response.bodyAsText()
        val quizResponse = Json.decodeFromString<QuizResponse>(responseBody)
        return quizResponse.data.quiz
    }

    override suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int?
    ): SuggestedMaterialsResponse {
        val response = client.get(urlString = BASE_URL + STUDY + SUGGESTED_MATERIALS) {
            parameter("userId", userId)
            parameter("limit", limit ?: 1)
        }
        val responseBody = response.bodyAsText()
        Log.i(RemoteApi::class.simpleName, Json.decodeFromString(responseBody))
        println(responseBody)
        return Json.decodeFromString(responseBody)
    }

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val AUTH = "/auth"
        private const val LOGIN = "/login"
        private const val DOCUMENT = "/document"
        private const val UPLOAD = "/upload"
        private const val RETRIEVE = "/retrieve"
        private const val STUDY = "/study"
        private const val FLASHCARDS = "/flashcards"
        private const val QUIZ = "/quiz"
        private const val SUBSCRIPTION = "/subscription"
        private const val CHAT = "/chat"
        private const val MESSAGE = "/message"
        private const val SUGGESTED_MATERIALS = "/suggestedMaterials"
    }
}
