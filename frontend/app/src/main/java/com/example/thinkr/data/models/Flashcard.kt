package com.example.thinkr.data.models

import kotlinx.serialization.Serializable


@Serializable
data class FlashcardsResponse(
    val data: FlashcardData
)

@Serializable
data class FlashcardData(
    val userId: String,
    val documentId: String,
    val flashcards: List<FlashcardItem>
)

@Serializable
data class FlashcardItem(
    val front: String,
    val back: String,
)