package com.example.thinkr.data.repositories.doc

import android.util.Log
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.SuggestedMaterials
import com.example.thinkr.data.remote.document.DocumentApi
import com.example.thinkr.data.remote.study.StudyApi
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class DocRepository(
    private val documentApi: DocumentApi,
    private val studyApi: StudyApi
) : IDocRepository {
    private val _uploadingDocuments = MutableStateFlow<List<Document>>(emptyList())

    override suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document> {
        return documentApi.getDocuments(userId, documentIds)
    }

    override fun getUploadingDocuments(): Flow<List<Document>> {
        return _uploadingDocuments.asStateFlow()
    }

    override suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String
    ): Boolean {
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
            val response = documentApi.uploadDocument(
                fileBytes = fileBytes,
                fileName = fileName,
                userId = userId,
                documentName = documentName,
                documentContext = documentContext
            )
            return true
        } catch (e: IOException) {
            _uploadingDocuments.value = _uploadingDocuments.value.filter {
                it.documentId != tempDocument.documentId
            }
            e.printStackTrace()
        } catch (e: ResponseException) {
            _uploadingDocuments.value = _uploadingDocuments.value.filter {
                it.documentId != tempDocument.documentId
            }
            e.printStackTrace()
        } catch (e: SerializationException) {
            _uploadingDocuments.value = _uploadingDocuments.value.filter {
                it.documentId != tempDocument.documentId
            }
            e.printStackTrace()
        } catch (e: Exception) {
            _uploadingDocuments.value = _uploadingDocuments.value.filter {
                it.documentId != tempDocument.documentId
            }
            e.printStackTrace()
        }

        return false
    }

    override suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int?
    ): SuggestedMaterials {
        return try {
            studyApi.getSuggestedMaterials(userId, limit)
        } catch (e: Exception) {
            Log.e("DocRepository", "Error fetching suggested materials", e)
            e.printStackTrace()
            SuggestedMaterials(emptyList(), emptyList())
        }
    }
}