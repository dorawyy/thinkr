package com.example.thinkr.data.remote.document

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.UploadResponse
import io.ktor.client.HttpClient
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/**
 * Implementation of the document API interface.
 *
 * Handles network requests related to document operations including
 * retrieving user documents and uploading new documents.
 *
 * @property client The HTTP client used to make network requests.
 */
class DocumentApi(private val client: HttpClient) : IDocumentApi {
    /**
     * Retrieves documents for a specific user.
     *
     * Makes a GET request to the document retrieve endpoint with the user's ID and
     * optional document IDs as parameters.
     *
     * @param userId The unique identifier of the user whose documents to retrieve.
     * @param documentIds Optional list of specific document IDs to retrieve. If null, returns all user documents.
     * @return List of Document objects belonging to the user.
     */
    override suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document> {
        val response = client.get(urlString = BASE_URL + DOCUMENT + RETRIEVE) {
            parameter("userId", userId)
            parameter(
                key = "documentId",
                value = if (documentIds == null) "" else documentIds[0]
            )
        }
        val responseBody = response.bodyAsText()
        val jsonResponse = Json.parseToJsonElement(responseBody).jsonObject
        val docs = jsonResponse["data"]?.jsonObject?.get("docs")?.jsonArray
        return docs?.map { Json.decodeFromJsonElement(Document.serializer(), it) } ?: emptyList()
    }

    /**
     * Uploads a document to the server.
     *
     * Makes a POST request to the document upload endpoint with the file content
     * and metadata as a multipart form.
     *
     * @param fileBytes The binary content of the file to upload.
     * @param fileName The name of the file being uploaded.
     * @param userId The unique identifier of the user uploading the document.
     * @param documentName The display name for the document.
     * @param documentContext Additional context information about the document.
     * @return UploadResponse containing the status and details of the upload operation.
     */
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

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val DOCUMENT = "/document"
        private const val UPLOAD = "/upload"
        private const val RETRIEVE = "/retrieve"
    }
}