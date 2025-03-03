package com.example.thinkr.ui.chat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.chat.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun loadChatHistory(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(userId = userId) }

            chatRepository.getChatHistory(userId)
                .onSuccess { chatData ->
                    _state.update { currentState ->
                        currentState.copy(
                            messages = chatData.messages
                                .filter { it.role != SYSTEM }
                                .map { chatMessage ->
                                    Message(
                                        id = chatMessage.timestamp,
                                        content = mutableStateOf(chatMessage.content),
                                        timestamp = parseTimestamp(chatMessage.timestamp),
                                        isSender = chatMessage.role == USER
                                    )
                                }
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }

    fun onSendMessage(content: String) {
        val userId = _state.value.userId
        if (content.isBlank() || userId.isEmpty()) return

        val newMessage = Message(
            id = System.currentTimeMillis().toString(),
            content = mutableStateOf(content),
            timestamp = System.currentTimeMillis(),
            isSender = true
        )

        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newMessage
            )
        }

        viewModelScope.launch {
            chatRepository.sendMessage(userId, content)
                .onSuccess { response ->
                    onReceiveMessage(response.content)
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }

    fun clearChatHistory() {
        val userId = _state.value.userId
        if (userId.isEmpty()) return

        viewModelScope.launch {
            chatRepository.clearChatHistory(userId)
                .onSuccess { _ ->
                    _state.update { currentState ->
                        currentState.copy(
                            messages = emptyList()
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }

    private fun onReceiveMessage(content: String) {
        val contentStream = mutableStateOf("")
        if (content.isBlank()) return

        val newMessage = Message(
            id = System.currentTimeMillis().toString(),
            content = contentStream,
            timestamp = System.currentTimeMillis(),
            isSender = false
        )

        _state.update { currentState ->
            currentState.copy(
                messages = currentState.messages + newMessage
            )
        }

        // Gives a "streaming" effect to the message
        viewModelScope.launch {
            content.forEach { char ->
                contentStream.value += char
                kotlinx.coroutines.delay(MESSAGE_STREAM_DELAY)
            }
        }
    }

    private fun parseTimestamp(timestamp: String): Long {
        return try {
            // Simple parsing, should be replaced with actual date parsing
            System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    companion object {
        private const val MESSAGE_STREAM_DELAY = 50L
        private const val SYSTEM = "system"
        private const val USER = "user"
    }
}
