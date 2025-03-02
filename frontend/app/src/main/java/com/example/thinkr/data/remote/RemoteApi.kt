@file:OptIn(InternalAPI::class)

package com.example.thinkr.data.remote

import com.example.thinkr.data.models.AuthResponse
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.LoginRequest
import com.example.thinkr.data.models.UploadResponse
import com.example.thinkr.data.repositories.subscription.SubscriptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.parameters
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.InputStream

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
        document: InputStream,
        userId: String,
        documentName: String,
        documentContext: String
    ): UploadResponse {
        val response = client.post(urlString = BASE_URL + DOCUMENT + UPLOAD) {
            body = MultiPartFormDataContent(
                formData {
                    append("document", document, Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.OctetStream)
                        append(HttpHeaders.ContentDisposition, "filename=\"$documentName\"")
                    })
                    append("userId", userId)
                    append("documentName", documentName)
                    append("context", documentContext)
                }
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

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val AUTH = "/auth"
        private const val LOGIN = "/login"
        private const val DOCUMENT = "/document"
        private const val UPLOAD = "/upload"
        private const val RETRIEVE = "/retrieve"
        private const val SUBSCRIPTION = "/subscription"
    }
}
