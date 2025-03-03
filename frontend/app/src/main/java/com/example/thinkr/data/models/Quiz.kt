package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

@Serializable
data class QuizResponse(
    val data: QuizData
)

@Serializable
data class QuizData(
    val userId: String,
    val documentId: String,
    val quiz: List<QuizItem>
)

@Serializable
data class QuizItem(
    val question: String,
    val answer: String,
    val options: Map<String, String>,
)
