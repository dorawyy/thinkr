package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class SuggestedMaterialsResponse(
    val data: SuggestedMaterials
)

@Serializable
data class SuggestedMaterials(
    val flashcards: List<FlashcardSuggestion>,
    val quizzes: List<QuizSuggestion>
)

@Serializable
data class FlashcardSuggestion(
    val userId: String,
    val documentId: String,
    val flashcards: List<FlashcardItem>
)

@Serializable
data class QuizSuggestion(
    val userId: String,
    val documentId: String,
    val quiz: List<QuizItem>
)
