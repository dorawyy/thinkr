package com.example.thinkr.data.remote.study

import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.models.SuggestedMaterials

/**
 * Interface for study-related API operations.
 *
 * Defines the contract for retrieving study materials including flashcards,
 * quiz items, and suggested learning resources through remote API endpoints.
 */
interface IStudyApi {
    suspend fun getFlashcards(userId: String, documentId: String): List<FlashcardItem>
    suspend fun getQuiz(userId: String, documentId: String): List<QuizItem>
    suspend fun getSuggestedMaterials(userId: String, limit: Int?): SuggestedMaterials
}
