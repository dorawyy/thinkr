package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing information about an uploaded document.
 *
 * Wraps the document data returned from the API after a successful upload.
 *
 * @property data The contained document data.
 */
@Serializable
data class UploadResponse(
    val data: DocumentData
)

/**
 * Container for document upload information.
 *
 * Holds the details of the document that was uploaded.
 *
 * @property docs The details of the uploaded document.
 */
@Serializable
data class DocumentData(
    val docs: DocumentDetails
)

/**
 * Detailed information about an uploaded document.
 *
 * Contains identifiers and metadata about the document.
 *
 * @property documentId The unique identifier assigned to the uploaded document.
 * @property uploadTime The timestamp when the document was uploaded.
 * @property activityGenerationComplete Boolean indicating whether learning activities have been generated for this document.
 * @property documentName The original name of the uploaded document.
 */
@Serializable
data class DocumentDetails(
    val documentId: String,
    val uploadTime: String,
    val activityGenerationComplete: Boolean,
    val documentName: String,
    val public: Boolean
)
