package com.example.thinkr.data.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data model representing a document in the system.
 *
 * Contains information about a document including its identifier, name, upload status,
 * and activity generation status. Includes transient properties for UI state management.
 *
 * @property documentId The unique identifier assigned to the document.
 * @property documentName The name of the document.
 * @property uploadTime The timestamp when the document was uploaded.
 * @property activityGenerationComplete Boolean indicating whether learning activities have been generated for this document.
 * @property isUploading Transient property indicating whether the document is currently being uploaded.
 * @property documentContext Transient property containing additional context or content for the document.
 */
@Serializable
data class Document(
    val documentId: String,
    val documentName: String,
    val uploadTime: String,
    val activityGenerationComplete: Boolean,
    val public: Boolean,
    @Transient
    val isUploading: Boolean = false,
    @Transient
    val documentContext: String = ""
)
