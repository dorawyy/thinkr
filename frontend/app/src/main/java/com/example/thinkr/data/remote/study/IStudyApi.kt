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
    /**
     * Retrieves flashcards for a specific document.
     *
     * @param userId The unique identifier of the user requesting the flashcards.
     * @param documentId The identifier of the document for which to generate flashcards.
     * @return List of FlashcardItem objects for study.
     */
    suspend fun getFlashcards(userId: String, documentId: String): List<FlashcardItem>

    /**
     * Retrieves quiz questions for a specific document.
     *
     * @param userId The unique identifier of the user requesting the quiz.
     * @param documentId The identifier of the document for which to generate quiz questions.
     * @return List of QuizItem objects containing questions and answers.
     */
    suspend fun getQuiz(userId: String, documentId: String): List<QuizItem>

    /**
     * Retrieves suggested learning materials for a user.
     *
     * @param userId The unique identifier of the user requesting suggestions.
     * @param limit Optional maximum number of suggestions to return, defaults to 1 if null.
     * @return SuggestedMaterials object containing recommended learning resources.
     */
    suspend fun getSuggestedMaterials(userId: String, limit: Int?): SuggestedMaterials
}
