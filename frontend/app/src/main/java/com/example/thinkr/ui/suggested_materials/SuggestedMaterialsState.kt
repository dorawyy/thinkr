package com.example.thinkr.ui.suggested_materials

import com.example.thinkr.data.models.SuggestedMaterials

/**
 * Data class representing the UI state for the suggested materials screen.
 *
 * @property suggestedMaterials The suggested materials, including flashcards and quizzes.
 * @property isLoading Boolean indicating whether the data is currently being loaded.
 */
data class SuggestedMaterialsState (
    val suggestedMaterials: SuggestedMaterials = SuggestedMaterials(
        flashcards = emptyList(),
        quizzes = emptyList()
    ),
    val isLoading: Boolean = true
)
