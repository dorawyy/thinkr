package com.example.thinkr.data.models

import kotlinx.serialization.Serializable

/**
 * Response model containing quiz data.
 *
 * Wraps the quiz data returned from the API.
 *
 * @property data The contained quiz data.
 */
@Serializable
data class QuizResponse(
    val data: QuizData
)

/**
 * Container for quiz information associated with a specific document.
 *
 * Holds user identifier, document identifier, and a list of quiz questions.
 *
 * @property userId The identifier of the user who owns the document.
 * @property documentId The identifier of the document from which the quiz was generated.
 * @property quiz The list of generated quiz questions.
 */
@Serializable
data class QuizData(
    val userId: String,
    val documentId: String,
    val quiz: List<QuizItem>
)

/**
 * Represents a single quiz question with its answer and options.
 *
 * Contains the question text, correct answer, and multiple-choice options.
 *
 * @property question The text of the quiz question.
 * @property answer The correct answer to the question.
 * @property options Map of option identifiers to option text for multiple-choice answers.
 */
@Serializable
data class QuizItem(
    val question: String,
    val answer: String,
    val options: Map<String, String>,
)
