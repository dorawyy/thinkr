package com.example.thinkr.data.repositories.flashcards

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.remote.study.StudyApi
import com.example.thinkr.data.repositories.user.UserRepository

/**
 * Implementation of the flashcards repository interface.
 *
 * Handles fetching flashcard items from a remote API based on a specific document.
 * Uses the user repository to authenticate requests with the user's Google ID.
 *
 * @property studyApi The API service used to make flashcard-related network requests.
 * @property userRepository Repository that provides the current user's authentication details.
 */
class FlashcardsRepository(
    private val studyApi: StudyApi,
    private val userRepository: UserRepository
) : IFlashcardsRepository {
    /**
     * Retrieves a list of flashcard items for a specific document.
     *
     * Fetches flashcard data from the remote study API using the current user's Google ID
     * and the document ID as parameters.
     *
     * @param documentItem The document for which to generate flashcard items.
     * @return List of FlashcardItem objects containing study material.
     * @throws IllegalStateException if no user is currently authenticated.
     */
    override suspend fun getFlashcards(documentItem: Document): List<FlashcardItem> {
        return studyApi.getFlashcards(userRepository.getUser()!!.googleId, documentItem.documentId)
    }
}
