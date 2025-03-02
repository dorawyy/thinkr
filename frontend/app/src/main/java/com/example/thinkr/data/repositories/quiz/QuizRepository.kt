package com.example.thinkr.data.repositories.quiz

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.remote.RemoteApi
import com.example.thinkr.data.repositories.user.UserRepository

class QuizRepository(private val remoteApi: RemoteApi, private val userRepository: UserRepository) : IQuizRepository {
    override suspend fun getQuiz(documentItem: Document): List<QuizItem> {
        return remoteApi.getQuiz(userRepository.getUser()!!.googleId, documentItem.documentId)
    }
}