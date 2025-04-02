package com.example.thinkr.app

import android.net.Uri
import com.example.thinkr.data.models.Document
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Sealed interface representing the navigation routes in the Thinkr application.
 *
 * This interface defines all possible navigation destinations within the app and
 * provides serialization support for route parameters. Each nested class or object
 * represents a specific screen or navigation endpoint.
 */
sealed interface Route {
    /**
     * Root navigation graph container for all routes.
     */
    @Serializable
    data object RouteGraph : Route

    /**
     * Landing screen route.
     */
    @Serializable
    data object Landing : Route

    /**
     * Home screen route.
     */
    @Serializable
    data object Home : Route

    /**
     * Suggested Materials screen route.
     */
    @Serializable
    data object SuggestedMaterials : Route

    /**
     * Document options screen route with a document parameter.
     *
     * @property documentItem The document to display options for.
     */
    @Serializable
    data class DocumentOptions(val documentItem: Document) : Route {
        internal companion object {
            const val ROUTE = "documentOptions/$DOCUMENT_JSON_PLACEHOLDER"
            const val ARGUMENT = DOCUMENT_JSON

            /**
             * Creates a route string with the document parameter encoded as JSON.
             *
             * @param document The document to encode in the route.
             * @return A route string with the encoded document parameter.
             */
            fun createRoute(document: Document): String {
                val json = Json.encodeToString(document)
                return ROUTE.replace(DOCUMENT_JSON_PLACEHOLDER, Uri.encode(json))
            }
        }
    }

    /**
     * Document upload screen route with URI parameter.
     *
     * @property selectedUri The URI of the selected document to upload.
     */
    @Serializable
    data class DocumentUpload(val selectedUri: String) : Route {
        internal companion object {
            const val ROUTE = "documentUpload/{selectedUri}"
            const val ARGUMENT = "selectedUri"

            /**
             * Creates a route string with the URI parameter.
             *
             * @param selectedUri The URI to encode in the route.
             * @return A route string with the encoded URI parameter.
             */
            fun createRoute(selectedUri: Uri): String {
                return ROUTE.replace("{selectedUri}", Uri.encode(selectedUri.toString()))
            }
        }
    }

    /**
     * User profile screen route.
     */
    @Serializable
    data object Profile : Route

    /**
     * Payment/subscription screen route.
     */
    @Serializable
    data object Payment : Route

    /**
     * Flashcards screen route with optional document and flashcard parameters.
     *
     * @property documentItem Optional document associated with the flashcards.
     * @property flashcardSuggestion Optional serialized flashcard suggestion data.
     */
    @Serializable
    data class Flashcards(val documentItem: Document? = null, val flashcardSuggestion: String? = null) : Route {
        internal companion object {
            const val ROUTE = "flashcards/$DOCUMENT_JSON_PLACEHOLDER/{flashcardSuggestion}"
            const val DOCUMENT_ARGUMENT = DOCUMENT_JSON
            const val FLASHCARD_ARGUMENT = "flashcardSuggestion"

            /**
             * Creates a route string with optional document and flashcard parameters.
             *
             * @param document Optional document to encode in the route.
             * @param flashcardSuggestion Optional flashcard suggestion to encode in the route.
             * @return A route string with encoded parameters.
             */
            fun createRoute(document: Document? = null, flashcardSuggestion: String? = null): String {
                val docJson = document?.let { Json.encodeToString(it) } ?: ""
                val flashcardJson = flashcardSuggestion ?: ""

                return ROUTE
                    .replace(DOCUMENT_JSON_PLACEHOLDER, Uri.encode(docJson))
                    .replace("{flashcardSuggestion}", Uri.encode(flashcardJson))
            }
        }
    }

    /**
     * Quiz screen route with optional document and quiz parameters.
     *
     * @property documentItem Optional document associated with the quiz.
     * @property quizSuggestion Optional serialized quiz suggestion data.
     */
    @Serializable
    data class Quiz(val documentItem: Document? = null, val quizSuggestion: String? = null) : Route {
        internal companion object {
            const val ROUTE = "quiz/$DOCUMENT_JSON_PLACEHOLDER/{quizSuggestion}"
            const val DOCUMENT_ARGUMENT = DOCUMENT_JSON
            const val QUIZ_ARGUMENT = "quizSuggestion"

            /**
             * Creates a route string with optional document and quiz parameters.
             *
             * @param document Optional document to encode in the route.
             * @param quizSuggestion Optional quiz suggestion to encode in the route.
             * @return A route string with encoded parameters.
             */
            fun createRoute(document: Document? = null, quizSuggestion: String? = null): String {
                val docJson = document?.let { Json.encodeToString(it) } ?: ""
                val quizJson = quizSuggestion ?: ""

                return ROUTE
                    .replace(DOCUMENT_JSON_PLACEHOLDER, Uri.encode(docJson))
                    .replace("{quizSuggestion}", Uri.encode(quizJson))
            }
        }
    }

    /**
     * Chat screen route with document parameter.
     *
     * @property documentItem The document to use as context for the chat.
     */
    @Serializable
    data class Chat(val documentItem: Document) : Route {
        internal companion object {
            const val ROUTE = "chat/$DOCUMENT_JSON_PLACEHOLDER"
            const val ARGUMENT = DOCUMENT_JSON

            /**
             * Creates a route string with the document parameter.
             *
             * @param document The document to encode in the route.
             * @return A route string with the encoded document parameter.
             */
            fun createRoute(document: Document): String {
                val json = Json.encodeToString(document)
                return ROUTE.replace(DOCUMENT_JSON_PLACEHOLDER, Uri.encode(json))
            }
        }
    }

    /**
     * Route constants for navigation actions.
     */
    companion object {
        private const val DOCUMENT_JSON = "documentJson"
        private const val DOCUMENT_JSON_PLACEHOLDER = "{$DOCUMENT_JSON}"
    }
}
