package com.example.thinkr.data.remote.study

import com.example.thinkr.data.models.FlashcardItem
import com.example.thinkr.data.models.FlashcardsResponse
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.models.QuizResponse
import com.example.thinkr.data.models.SuggestedMaterials
import com.example.thinkr.data.models.SuggestedMaterialsResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class StudyApi(private val client: HttpClient) : IStudyApi {
    override suspend fun getFlashcards(userId: String, documentId: String): List<FlashcardItem> {
        val response = client.get(urlString = BASE_URL + STUDY + FLASHCARDS) {
            parameter("userId", userId)
            parameter("documentId", documentId)
        }
        val responseBody = response.bodyAsText()
        val flashcardsResponse = Json.decodeFromString<FlashcardsResponse>(responseBody)
        return flashcardsResponse.data.flashcards
    }

    override suspend fun getQuiz(userId: String, documentId: String): List<QuizItem> {
        val response = client.get(urlString = BASE_URL + STUDY + QUIZ) {
            parameter("userId", userId)
            parameter("documentId", documentId)
        }
        val responseBody = response.bodyAsText()
        val quizResponse = Json.decodeFromString<QuizResponse>(responseBody)
        return quizResponse.data.quiz
    }

    override suspend fun getSuggestedMaterials(
        userId: String,
        limit: Int?
    ): SuggestedMaterials {
        val response = client.get(urlString = BASE_URL + STUDY + SUGGESTED_MATERIALS) {
            parameter("userId", userId)
            parameter("limit", limit ?: 1)
        }
        println("suggested materials GET request $response")
        val responseBody = response.bodyAsText()
        println("suggested materials GET request $responseBody")
        return Json.decodeFromString<SuggestedMaterialsResponse>(responseBody).data
    }

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val STUDY = "/study"
        private const val FLASHCARDS = "/flashcards"
        private const val QUIZ = "/quiz"
        private const val SUGGESTED_MATERIALS = "/suggestedMaterials"
    }
}
