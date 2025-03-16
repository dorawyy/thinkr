package com.example.thinkr.ui.flashcards

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.repositories.flashcards.FlashcardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel that manages the flashcards screen state and operations.
 *
 * Handles loading flashcards from a document or from suggested flashcards,
 * and manages navigation from the flashcards screen.
 *
 * @property flashcardsRepository Repository for accessing and retrieving flashcard data.
 */
class FlashcardsViewModel(private val flashcardsRepository: FlashcardsRepository) : ViewModel() {
    private val _state = MutableStateFlow(FlashcardsScreenState())
    val state = _state.asStateFlow()

    /**
     * Loads flashcards for a specific document from the repository.
     *
     * Updates the state with the retrieved flashcards.
     *
     * @param documentItem The document to load flashcards for.
     */
    suspend fun loadFlashcards(documentItem: Document) {
        _state.update { it.copy(flashcards = flashcardsRepository.getFlashcards(documentItem)) }
    }

    /**
     * Loads pre-generated flashcards without accessing the repository.
     *
     * Used for displaying suggested flashcards provided from another screen.
     *
     * @param flashcards List of flashcard items to display.
     */
    fun loadSuggestedFlashcards(flashcards: List<FlashcardItem>) {
        _state.update { it.copy(flashcards = flashcards) }
    }

    /**
     * Handles back navigation from the flashcards screen.
     *
     * @param navController Navigation controller to handle screen transitions.
     */
    fun onBackPressed(navController: NavController) {
        navController.popBackStack()
    }
}
