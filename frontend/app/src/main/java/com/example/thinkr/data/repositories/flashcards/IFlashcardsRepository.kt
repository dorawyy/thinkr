package com.example.thinkr.data.repositories.flashcards

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem

/**
 * Interface for managing flashcard-related operations.
 *
 * Defines the contract for retrieving flashcard items generated from user documents.
 * These items are typically retrieved from a remote study API.
 */
interface IFlashcardsRepository {
    /**
     * Retrieves a list of flashcard items for a specific document.
     *
     * @param documentItem The document for which to generate flashcard items.
     * @return List of FlashcardItem objects containing study material.
     * @throws IllegalStateException if no user is currently authenticated.
     */
    suspend fun getFlashcards(documentItem: Document): List<FlashcardItem>
}
