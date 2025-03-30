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

/**
 * Implementation of the document repository interface.
 *
 * Manages document operations including retrieving user documents, tracking upload progress,
 * uploading documents to the server, and fetching suggested learning materials.
 *
 * @property documentApi The API service for document-related network requests.
 * @property studyApi The API service for study-related network requests.
 */
class DocRepository(
    private val documentApi: DocumentApi,
    private val studyApi: StudyApi
) : IDocRepository {
    private val _uploadingDocuments = MutableStateFlow<List<Document>>(emptyList())

    /**
     * Retrieves documents for a specific user from the remote API.
     *
     * @param userId The unique identifier of the user whose documents to retrieve.
     * @param documentIds Optional list of specific document IDs to retrieve.
     * @return List of Document objects belonging to the user.
     */
    override suspend fun getDocuments(
        userId: String,
        documentIds: List<String>?
    ): List<Document> {
        return documentApi.getDocuments(userId, documentIds)
    }

    /**
     * Provides access to the flow of documents currently being uploaded.
     *
     * @return Flow emitting lists of Document objects that are in the uploading state.
     */
    override fun getUploadingDocuments(): Flow<List<Document>> {
        return _uploadingDocuments.asStateFlow()
    }

    /**
     * Uploads a document to the server with progress tracking.
     *
     * Creates a temporary document entry to track upload progress and attempts to upload
     * the document to the server. Removes the temporary entry if the upload fails.
     *
     * @param fileBytes The binary content of the file to upload.
     * @param fileName The name of the file being uploaded.
     * @param userId The unique identifier of the user uploading the document.
     * @param documentName The display name for the document.
     * @param documentContext Additional context information about the document.
     * @param documentPublic Boolean indicating whether the document should be publicly accessible.
     * @return Boolean indicating whether the upload was successful.
     */
    override suspend fun uploadDocument(
        fileBytes: ByteArray,
        fileName: String,
        userId: String,
        documentName: String,
        documentContext: String,
        documentPublic: Boolean
    ): Boolean {
        val tempDocument = Document(
            documentId = "temp_${System.currentTimeMillis()}",
            documentName = documentName,
            uploadTime = System.currentTimeMillis().toString(),
            activityGenerationComplete = false,
            isUploading = true,
            documentContext = documentContext,
            public = documentPublic
        )

        _uploadingDocuments.value += tempDocument

        try {
            documentApi.uploadDocument(
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
        }

        return false
    }

    /**
     * Retrieves suggested learning materials for a user from the study endpoint.
     *
     * @param userId The unique identifier of the user to get suggestions for.
     * @param limit Optional maximum number of suggestions to return.
     * @return SuggestedMaterials object containing recommended learning resources.
     *         Returns empty lists if an error occurs during the request.
     */
    override suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int?
    ): SuggestedMaterials {
        return try {
            studyApi.getSuggestedMaterials(userId, limit)
        } catch (e: IOException) {
            handleFetchError(e)
        } catch (e: ResponseException) {
            handleFetchError(e)
        } catch (e: SerializationException) {
            handleFetchError(e)
        }
    }

    private fun handleFetchError(e: Exception): SuggestedMaterials {
        Log.e("DocRepository", "Error fetching suggested materials", e)
        e.printStackTrace()
        return SuggestedMaterials(emptyList(), emptyList())
    }
}