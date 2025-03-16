package com.example.thinkr.ui.flashcards

import com.example.thinkr.data.models.FlashcardItem

/**
 * Data class representing the UI state of the flashcards screen.
 *
 * Contains information about the current set of flashcards being displayed.
 *
 * @property flashcards List of flashcard items to be displayed in the UI.
 *           Empty list by default when no flashcards are loaded.
 */
data class FlashcardsScreenState(
    var flashcards: List<FlashcardItem> = emptyList()
)
