package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing flashcard data.
 *
 * Wraps the flashcard data returned from the API.
 *
 * @property data The contained flashcard data.
 */
@Serializable
data class FlashcardsResponse(
    val data: FlashcardData
)

/**
 * Container for flashcard information associated with a specific document.
 *
 * Holds user identifier, document identifier, and a list of flashcard items.
 *
 * @property userId The identifier of the user who owns the document.
 * @property documentId The identifier of the document from which the flashcards were generated.
 * @property flashcards The list of generated flashcard items.
 */
@Serializable
data class FlashcardData(
    val userId: String,
    val documentId: String,
    val flashcards: List<FlashcardItem>
)

/**
 * Represents a single flashcard with front and back content.
 *
 * Contains the text for both sides of a flashcard.
 *
 * @property front The text shown on the front/question side of the flashcard.
 * @property back The text shown on the back/answer side of the flashcard.
 */
@Serializable
data class FlashcardItem(
    val front: String,
    val back: String
)
