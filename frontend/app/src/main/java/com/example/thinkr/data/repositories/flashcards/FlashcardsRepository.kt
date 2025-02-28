package com.example.thinkr.data.repositories.flashcards

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.remote.RemoteApi

class FlashcardsRepository(private val remoteApi: RemoteApi): IFlashcardsRepository {
    override fun getFlashcards(documentItem: Document): List<FlashcardItem> {
        // TODO: replace with get request
        return listOf(
            FlashcardItem("Question 1", "Answer 1"),
            FlashcardItem("Question 2", "Answer 2"),
            FlashcardItem("Question 3", "Answer 3")
        )
    }
}