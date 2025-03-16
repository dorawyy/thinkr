package com.example.thinkr.ui.quiz

import com.example.thinkr.data.models.QuizItem

/**
 * Data class representing the state of a quiz session.
 *
 * @property quiz List of quiz items that make up the current quiz.
 * @property selectedAnswers List of selected answer identifiers corresponding to each quiz item.
 * @property started Boolean indicating whether the quiz has been started.
 * @property revealAnswer Boolean indicating whether answers should be revealed (typically after quiz completion).
 * @property totalScore The total score achieved in the quiz.
 * @property totalTimeSeconds The total time allocated for the quiz in seconds.
 */
data class QuizState(
    var quiz: List<QuizItem> = emptyList(),
    var selectedAnswers: List<String> = emptyList(),
    var started: Boolean = false,
    var revealAnswer: Boolean = false,
    var totalScore: Int = 0,
    var totalTimeSeconds: Int = 0
)
