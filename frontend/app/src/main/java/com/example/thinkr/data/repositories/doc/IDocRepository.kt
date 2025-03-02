package com.example.thinkr.data.repositories.doc

import com.example.thinkr.data.models.Document
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

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
}
