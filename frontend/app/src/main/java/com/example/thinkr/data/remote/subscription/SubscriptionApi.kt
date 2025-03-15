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

class SubscriptionApi(private val client: HttpClient) : ISubscriptionApi {
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
