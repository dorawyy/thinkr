package com.example.thinkr.ui.quiz

import com.example.thinkr.data.models.QuizItem

data class QuizState(
    var quiz: List<QuizItem> = emptyList(),
    var selectedAnswers: List<String> = emptyList(),
    var started: Boolean = false,
    var revealAnswer: Boolean = false,
    var totalScore: Int = 0,
    var totalTimeSeconds: Int = 0
)
