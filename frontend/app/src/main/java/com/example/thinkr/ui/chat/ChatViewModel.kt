package com.example.thinkr.ui.chat


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.models.ChatMetadata
import com.example.thinkr.data.repositories.chat.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val chatRepository: ChatRepository) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    init {
        // TODO: Remove this and replace it with the messages received from the backend
        _state.update { currentState ->
            currentState.copy(
                messages = listOf(
                    Message(
                        id = "1",
                        content = mutableStateOf("Hello there!"),
                        timestamp = System.currentTimeMillis() - 3600000,
                        isSender = false
                    ),
                    Message(
                        id = "2",
                        content = mutableStateOf("Hi! How are you?"),
                        timestamp = System.currentTimeMillis() - 3500000,
                        isSender = true
                    ),
                    Message(
                        id = "3",
                        content = mutableStateOf("I'm doing great, thanks for asking. How about you?"),
                        timestamp = System.currentTimeMillis() - 3400000,
                        isSender = false
                    )
                )
            )
        }
    }

    fun createChatSession(userId: String, documentId: String? = null) {
        viewModelScope.launch {
            val metadata = ChatMetadata(
                source = "mobile",
                topic = "general",
                documentId = documentId
            )

            chatRepository.createChatSession(userId, metadata)
                .onSuccess { session ->
                    _state.update { currentState ->
                        currentState.copy(
                            sessionId = session.sessionId,
                            messages = session.messages
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
                    // Handle error
                    error.printStackTrace()
                }
        }
    }

    fun loadChatSession(sessionId: String) {
        viewModelScope.launch {
            chatRepository.getChatSession(sessionId)
                .onSuccess { session ->
                    _state.update { currentState ->
                        currentState.copy(
                            sessionId = session.sessionId,
                            messages = session.messages
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
        if (content.isBlank() || _state.value.sessionId.isEmpty()) return

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
            chatRepository.sendMessage(_state.value.sessionId, content)
                .onSuccess { response ->
                    onReceiveMessage(response)
                }
                .onFailure { error ->
                    // Handle error
                    error.printStackTrace()
                }
        }
    }

    fun onReceiveMessage(content: String) {
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

    fun deleteChatSession() {
        val sessionId = _state.value.sessionId
        if (sessionId.isEmpty()) return

        viewModelScope.launch {
            chatRepository.deleteChatSession(sessionId)
                .onSuccess { _ ->
                    _state.update { currentState ->
                        currentState.copy(
                            sessionId = "",
                            messages = emptyList()
                        )
                    }
                }
                .onFailure { error ->
                    // Handle error
                    error.printStackTrace()
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
