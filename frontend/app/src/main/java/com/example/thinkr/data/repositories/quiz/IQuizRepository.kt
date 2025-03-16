package com.example.thinkr.data.repositories.quiz

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.QuizItem

/**
 * Interface for managing quiz-related operations.
 *
 * Defines the contract for retrieving quiz items generated from user documents.
 * These items are typically retrieved from a remote study API.
 */
interface IQuizRepository {
    /**
     * Retrieves a list of quiz items for a specific document.
     *
     * @param documentItem The document for which to generate quiz items.
     * @return List of QuizItem objects containing questions and answers.
     * @throws IllegalStateException if no user is currently authenticated.
     */
    suspend fun getQuiz(documentItem: Document): List<QuizItem>
}
