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
    suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document>

    fun getUploadingDocuments(): Flow<List<Document>>

    suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): Boolean

    suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int? = null
    ): SuggestedMaterials
}
