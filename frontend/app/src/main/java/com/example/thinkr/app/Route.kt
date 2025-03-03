package com.example.thinkr.app

import android.net.Uri
import com.example.thinkr.data.models.Document
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

sealed interface Route {
    @Serializable
    data object RouteGraph : Route

    @Serializable
    data object Landing : Route

    @Serializable
    data object Home : Route

    @Serializable
    data class DocumentOptions(val documentItem: Document) : Route {
        companion object {
            const val ROUTE = "documentOptions/{documentJson}"
            const val ARGUMENT = "documentJson"
            fun createRoute(document: Document): String {
                val json = Json.encodeToString(document)
                return ROUTE.replace("{documentJson}", Uri.encode(json))
            }
        }
    }

    @Serializable
    data class DocumentUpload(val selectedUri: String) : Route {
        companion object {
            const val ROUTE = "documentUpload/{selectedUri}"
            const val ARGUMENT = "selectedUri"
            fun createRoute(selectedUri: Uri): String {
                return ROUTE.replace("{selectedUri}", Uri.encode(selectedUri.toString()))
            }
        }
    }

    @Serializable
    data object Profile : Route

    @Serializable
    data object Payment : Route

    @Serializable
    data class Flashcards(val documentItem: Document? = null, val flashcardSuggestion: String? = null) : Route {
        companion object {
            const val ROUTE = "flashcards/{documentJson}/{flashcardSuggestion}"
            const val DOCUMENT_ARGUMENT = "documentJson"
            const val FLASHCARD_ARGUMENT = "flashcardSuggestion"

            fun createRoute(document: Document? = null, flashcardSuggestion: String? = null): String {
                val docJson = document?.let { Json.encodeToString(it) } ?: ""
                val flashcardJson = flashcardSuggestion ?: ""

                return ROUTE
                    .replace("{documentJson}", Uri.encode(docJson))
                    .replace("{flashcardSuggestion}", Uri.encode(flashcardJson))
            }
        }
    }

    @Serializable
    data class Quiz(val documentItem: Document? = null, val quizSuggestion: String? = null) : Route {
        companion object {
            const val ROUTE = "quiz/{documentJson}/{quizSuggestion}"
            const val DOCUMENT_ARGUMENT = "documentJson"
            const val QUIZ_ARGUMENT = "quizSuggestion"

            fun createRoute(document: Document? = null, quizSuggestion: String? = null): String {
                val docJson = document?.let { Json.encodeToString(it) } ?: ""
                val quizJson = quizSuggestion ?: ""

                return ROUTE
                    .replace("{documentJson}", Uri.encode(docJson))
                    .replace("{quizSuggestion}", Uri.encode(quizJson))
            }
        }
    }

    @Serializable
    data class Chat(val documentItem: Document) : Route {
        companion object {
            const val ROUTE = "chat/{documentJson}"
            const val ARGUMENT = "documentJson"
            fun createRoute(document: Document): String {
                val json = Json.encodeToString(document)
                return ROUTE.replace("{documentJson}", Uri.encode(json))
            }
        }
    }
}
