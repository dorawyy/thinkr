package com.example.thinkr.data.repositories.flashcards

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.remote.RemoteApi
import com.example.thinkr.data.repositories.user.UserRepository

class FlashcardsRepository(private val remoteApi: RemoteApi, private val userRepository: UserRepository) : IFlashcardsRepository {
    override suspend fun getFlashcards(documentItem: Document): List<FlashcardItem> {
        return remoteApi.getFlashcards(userRepository.getUser()!!.googleId, documentItem.documentId)
    }
}