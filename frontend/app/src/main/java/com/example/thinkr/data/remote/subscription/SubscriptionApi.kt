package com.example.thinkr.data.remote.subscription

import com.example.thinkr.data.models.SubscriptionResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/**
 * Implementation of the subscription API interface.
 *
 * Handles network requests related to user subscriptions including
 * subscribing users and retrieving their subscription status.
 *
 * @property client The HTTP client used to make network requests.
 */
class SubscriptionApi(private val client: HttpClient) : ISubscriptionApi {
    /**
     * Subscribes a user to the premium service.
     *
     * Makes a POST request to the subscription endpoint with the user's ID.
     *
     * @param userId The unique identifier of the user to subscribe.
     * @return SubscriptionResponse containing subscription details and status.
     */
    override suspend fun subscribe(
        userId: String
    ): SubscriptionResponse {
        val response = client.post(urlString = BASE_URL + SUBSCRIPTION) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("userId" to userId))
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    /**
     * Retrieves the current subscription status for a user.
     *
     * Makes a GET request to the subscription endpoint with the user's ID as a parameter.
     *
     * @param userId The unique identifier of the user whose subscription status to check.
     * @return SubscriptionResponse containing subscription details and status.
     */
    override suspend fun getSubscriptionStatus(
        userId: String
    ): SubscriptionResponse {
        val response = client.get(urlString = BASE_URL + SUBSCRIPTION) {
            parameter("userId", userId)
        }
        val responseBody = response.bodyAsText()
        return Json.decodeFromString(responseBody)
    }

    private companion object {
        private const val BASE_URL = "https://vazrwha8g4.execute-api.us-east-2.amazonaws.com"
        private const val SUBSCRIPTION = "/subscription"
    }
}
