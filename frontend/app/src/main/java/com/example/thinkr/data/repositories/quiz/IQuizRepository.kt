package com.example.thinkr.data.repositories.quiz

import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.QuizItem

interface IQuizRepository {
    suspend fun getQuiz(documentItem: Document): List<QuizItem>
}