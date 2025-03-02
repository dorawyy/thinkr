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

class DocumentOptionsViewModel(private val docRepository: DocRepository, private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(DocumentOptionsState())
    val state = _state.asStateFlow()

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

    private fun onReady() {
        _state.update { it.copy(isReady = true) }
    }

    companion object {
        const val RETRIES = 60
        const val TIMEOUT = 1000L
    }
}
