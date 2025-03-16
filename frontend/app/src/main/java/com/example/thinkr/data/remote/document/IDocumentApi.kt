package com.example.thinkr.data.remote.document

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.UploadResponse

/**
 * Interface for document-related API operations.
 *
 * Defines the contract for retrieving user documents and uploading new documents
 * through remote API endpoints.
 */
interface IDocumentApi {
    /**
     * Retrieves documents for a specific user.
     *
     * @param userId The unique identifier of the user whose documents to retrieve.
     * @param documentIds Optional list of specific document IDs to retrieve. If null, returns all user documents.
     * @return List of Document objects belonging to the user.
     */
    suspend fun getDocuments(userId: String, documentIds: List<String>?): List<Document>

    /**
     * Uploads a document to the server.
     *
     * @param fileBytes The binary content of the file to upload.
     * @param fileName The name of the file being uploaded.
     * @param userId The unique identifier of the user uploading the document.
     * @param documentName The display name for the document.
     * @param documentContext Additional context information about the document.
     * @return UploadResponse containing the status and details of the upload operation.
     */
    suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): UploadResponse
}
