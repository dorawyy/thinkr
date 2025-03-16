package com.example.thinkr.data.repositories.quiz

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.remote.study.StudyApi
import com.example.thinkr.data.repositories.user.UserRepository

/**
 * Implementation of the quiz repository interface.
 *
 * Handles fetching quiz items from a remote API based on a specific document.
 * Uses the user repository to authenticate requests with the user's Google ID.
 *
 * @property studyApi The API service used to make quiz-related network requests.
 * @property userRepository Repository that provides the current user's authentication details.
 */
class QuizRepository(
    private val studyApi: StudyApi,
    private val userRepository: UserRepository
) : IQuizRepository {
    /**
     * Retrieves a list of quiz items for a specific document.
     *
     * Fetches quiz data from the remote study API using the current user's Google ID
     * and the document ID as parameters.
     *
     * @param documentItem The document for which to generate quiz items.
     * @return List of QuizItem objects containing questions and answers.
     * @throws IllegalStateException if no user is currently authenticated.
     */
    override suspend fun getQuiz(documentItem: Document): List<QuizItem> {
        return studyApi.getQuiz(userRepository.getUser()!!.googleId, documentItem.documentId)
    }
}
