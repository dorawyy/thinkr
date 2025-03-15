package com.example.thinkr.data.repositories.quiz

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.remote.study.StudyApi
import com.example.thinkr.data.repositories.user.UserRepository

class QuizRepository(
    private val studyApi: StudyApi,
    private val userRepository: UserRepository
) : IQuizRepository {
    override suspend fun getQuiz(documentItem: Document): List<QuizItem> {
        return studyApi.getQuiz(userRepository.getUser()!!.googleId, documentItem.documentId)
    }
}