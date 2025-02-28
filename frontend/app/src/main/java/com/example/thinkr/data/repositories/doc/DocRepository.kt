package com.example.thinkr.data.repositories.doc

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.remote.RemoteApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream

class DocRepository(private val remoteApi: RemoteApi): IDocRepository {
    private val _uploadingDocuments = MutableStateFlow<List<Document>>(emptyList())

    override suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document> {
        return remoteApi.getDocuments(userId, documentIds)
    }

    override fun getUploadingDocuments(): Flow<List<Document>> {
        return _uploadingDocuments.asStateFlow()
    }

    override suspend fun uploadDocument(
        document: InputStream,
        userId: String,
        documentName: String,
        documentContext: String
    ) {
        remoteApi.uploadDocument(
            document = document,
            userId = userId,
            documentName = documentName,
            documentContext = documentContext
        )

        val tempDocument = Document(
            documentId = "temp_${System.currentTimeMillis()}",
            documentName = documentName,
            uploadTime = System.currentTimeMillis().toString(),
            activityGenerationComplete = false,
            isUploading = true,
            documentContext = documentContext
        )

        _uploadingDocuments.value += tempDocument

        try {
            remoteApi.uploadDocument(
                document = document,
                userId = userId,
                documentName = documentName,
                documentContext = documentContext
            )
            _uploadingDocuments.value = _uploadingDocuments.value.filter { it.documentId != tempDocument.documentId }
        } catch (e: Exception) {
            _uploadingDocuments.value = _uploadingDocuments.value.filter { it.documentId != tempDocument.documentId }
            e.printStackTrace()
        }
    }
}