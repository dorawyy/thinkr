package com.example.thinkr.ui.home

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.SuggestedMaterials

data class HomeScreenState(
    val showDialog: Boolean = false,
    val selectedDocument: Document? = null,
    val retrievedDocuments: List<Document> = emptyList(),
    val uploadingDocuments: List<Document> = emptyList(),
    val suggestedMaterials: SuggestedMaterials = SuggestedMaterials(
        flashcards = emptyList(),
        quizzes = emptyList()
    )
)
