package com.example.thinkr.ui.document_upload

import android.net.Uri

/**
 * Data class representing the UI state of the document upload screen.
 *
 * Contains information about the document being uploaded, including its name,
 * context description, and file URI.
 *
 * @property name The name of the document being uploaded. Defaults to "document name".
 * @property context Additional context or description for the document. Defaults to "context".
 * @property uri The URI pointing to the document file to be uploaded. Defaults to an empty URI.
 */
data class DocumentUploadScreenState(
    val name: String = "document name",
    val context: String = "context",
    val uri: Uri = Uri.EMPTY
)
