package com.example.thinkr.data.repositories.flashcards

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem

interface IFlashcardsRepository {
    suspend fun getFlashcards(documentItem: Document): List<FlashcardItem>
}