package com.example.thinkr.ui.flashcards

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.repositories.flashcards.FlashcardsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FlashcardsViewModel(private val flashcardsRepository: FlashcardsRepository) : ViewModel() {
    private val _state = MutableStateFlow(FlashcardsState())
    val state = _state.asStateFlow()

    suspend fun loadFlashcards(documentItem: Document) {
        _state.update { it.copy(flashcards = flashcardsRepository.getFlashcards(documentItem)) }
    }

    fun loadSuggestedFlashcards(flashcards: List<FlashcardItem>) {
        _state.update { it.copy(flashcards = flashcards) }
    }

    fun onBackPressed(navController: NavController) {
        navController.popBackStack()
    }
}
