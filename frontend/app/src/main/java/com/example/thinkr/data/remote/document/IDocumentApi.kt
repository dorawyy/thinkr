package com.example.thinkr.data.remote.document

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.UploadResponse

interface IDocumentApi {
    suspend fun getDocuments(userId: String, documentIds: List<String>?): List<Document>
    suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): UploadResponse
}
