package com.example.thinkr.ui.document_options

/**
 * Data class representing the UI state of the document options screen.
 *
 * Contains information about the document's processing status, indicating whether
 * the document is ready for interaction with various features like flashcards
 * and other learning activities.
 *
 * @property isReady Boolean flag indicating whether the document's activities have been
 *                  fully generated and are ready for user interaction. Defaults to false.
 */
data class DocumentOptionsScreenState(
    val isReady: Boolean = false
)
