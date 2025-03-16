package com.example.thinkr.ui.document_options

import androidx.lifecycle.ViewModel
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/**
 * ViewModel that manages the document options screen state and operations.
 *
 * Handles checking document processing status, determining user subscription status,
 * and updating the UI state for the document options screen.
 *
 * @property docRepository Repository for retrieving document information.
 * @property userRepository Repository for accessing current user information.
 */
class DocumentOptionsViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentOptionsScreenState())
    val state = _state.asStateFlow()

    /**
     * Checks if the provided document's activity generation is complete.
     *
     * Makes repeated requests to the repository to check document status,
     * with a delay between attempts. Updates the state to ready when
     * activity generation is confirmed complete.
     *
     * @param documentItem The document to check for readiness.
     */
    suspend fun checkIfDocumentIsReady(documentItem: Document) {
        for (i in 0 until RETRIES) {
            val response = docRepository.getDocuments(
                userRepository.getUser()!!.googleId,
                listOf(documentItem.documentId)
            ).getOrNull(0)
            if (response!!.activityGenerationComplete) {
                onReady()
                return
            }
            withContext(Dispatchers.IO) {
                Thread.sleep(TIMEOUT)
            }
        }
    }

    /**
     * Determines if the current user has a premium subscription.
     *
     * @return True if the user has an active subscription, false otherwise.
     */
    fun isPremium(): Boolean {
        return userRepository.getUser()!!.subscribed
    }

    private fun onReady() {
        _state.update { it.copy(isReady = true) }
    }

    internal companion object {
        const val RETRIES = 60
        const val TIMEOUT = 1000L
    }
}
