package com.example.thinkr.ui.home

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.SuggestedMaterials

/**
 * Data class representing the UI state of the home screen.
 *
 * Contains information about documents, dialog visibility, and suggested learning materials.
 *
 * @property showDialog Boolean indicating whether the document dialog is currently displayed.
 * @property selectedDocument The currently selected document, or null if no document is selected.
 * @property retrievedDocuments List of documents retrieved from the repository.
 * @property uploadingDocuments List of documents currently being uploaded.
 */
data class HomeScreenState(
    val showDialog: Boolean = false,
    val selectedDocument: Document? = null,
    val retrievedDocuments: List<Document> = emptyList(),
    val uploadingDocuments: List<Document> = emptyList(),
)
