package com.example.thinkr.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory for creating configured HTTP clients.
 *
 * Provides a standard way to create and configure HttpClient instances with
 * common plugins and settings for API communication.
 */
object HttpClientFactory {
    /**
     * Creates a configured HTTP client with the specified engine.
     *
     * Configures the client with:
     * - Content negotiation for JSON serialization/deserialization
     * - Timeout settings for socket and request operations
     * - Logging of HTTP requests and responses
     * - Default content type set to application/json
     *
     * @param engine The HTTP client engine implementation to use.
     * @return A configured HttpClient instance ready for API communication.
     */
    fun create(engine: HttpClientEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }

                level = LogLevel.ALL
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
