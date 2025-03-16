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
    suspend fun getFlashcards(documentItem: Document): List<FlashcardItem>
}
