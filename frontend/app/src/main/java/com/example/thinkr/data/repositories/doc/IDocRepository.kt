package com.example.thinkr.data.repositories.doc

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.SuggestedMaterials
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing document-related operations.
 *
 * Defines the contract for retrieving, uploading, and managing user documents.
 * Provides access to document lists, uploading progress, and suggested learning materials.
 */
interface IDocRepository {
    /**
     * Retrieves documents for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose documents to retrieve.
     * @param documentIds Optional list of specific document IDs to retrieve.
     * @return List of Document objects belonging to the user.
     */
    suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document>

    /**
     * Provides access to the flow of documents currently being uploaded.
     *
     * @return Flow emitting lists of Document objects that are in the uploading state.
     */
    fun getUploadingDocuments(): Flow<List<Document>>

    /**
     * Uploads a document to the server with progress tracking.
     *
     * @param fileBytes The binary content of the file to upload.
     * @param fileName The name of the file being uploaded.
     * @param userId The unique identifier of the user uploading the document.
     * @param documentName The display name for the document.
     * @param documentContext Additional context information about the document.
     * @param documentPublic Boolean indicating whether the document should be publicly accessible.
     * @return Boolean indicating whether the upload was successful.
     */
    suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String,
        documentPublic: Boolean
    ): Boolean

    /**
     * Retrieves suggested learning materials for a user from the study endpoint.
     *
     * @param userId The unique identifier of the user to get suggestions for.
     * @param limit Optional maximum number of suggestions to return.
     * @return SuggestedMaterials object containing recommended learning resources.
     *         Returns empty lists if an error occurs during the request.
     */
    suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int? = null
    ): SuggestedMaterials
}
