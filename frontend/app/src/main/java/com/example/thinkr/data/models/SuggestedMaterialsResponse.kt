package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing suggested learning materials.
 *
 * Wraps the suggested materials data returned from the API.
 *
 * @property data The contained suggested materials data.
 */
@Serializable
data class SuggestedMaterialsResponse(
    val data: SuggestedMaterials
)

/**
 * Container for different types of suggested learning materials.
 *
 * Holds collections of flashcards and quizzes that are recommended for the user.
 *
 * @property flashcards List of suggested flashcard collections.
 * @property quizzes List of suggested quiz collections.
 */
@Serializable
data class SuggestedMaterials(
    val flashcards: List<FlashcardSuggestion>,
    val quizzes: List<QuizSuggestion>
)

/**
 * Represents a suggested collection of flashcards.
 *
 * Contains flashcards generated from a specific user document.
 *
 * @property userId The identifier of the user who owns the document.
 * @property documentId The identifier of the document from which flashcards were generated.
 * @property flashcards The list of generated flashcard items.
 */
@Serializable
data class FlashcardSuggestion(
    val userId: String,
    val documentId: String,
    val flashcards: List<FlashcardItem>
)

/**
 * Represents a suggested quiz.
 *
 * Contains quiz questions generated from a specific user document.
 *
 * @property userId The identifier of the user who owns the document.
 * @property documentId The identifier of the document from which the quiz was generated.
 * @property quiz The list of generated quiz questions.
 */
@Serializable
data class QuizSuggestion(
    val userId: String,
    val documentId: String,
    val quiz: List<QuizItem>
)
