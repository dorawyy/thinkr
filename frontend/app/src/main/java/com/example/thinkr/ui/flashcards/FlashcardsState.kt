package com.example.thinkr.ui.flashcards

import com.example.thinkr.data.models.FlashcardItem

data class FlashcardsState(
    var flashcards: List<FlashcardItem> = emptyList()
)