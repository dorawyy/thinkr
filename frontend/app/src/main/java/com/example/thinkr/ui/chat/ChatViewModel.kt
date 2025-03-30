package com.example.thinkr.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.chat.ChatRepository
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * ViewModel that manages the chat screen state and operations.
 *
 * Handles loading chat history, sending messages, receiving responses,
 * and clearing chat history. Manages streaming effect for received messages
 * to provide a more natural conversation experience.
 *
 * @property chatRepository Repository for chat data operations.
 * @property userRepository Repository for accessing current user information.
 */
class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ChatScreenState())
    val state: StateFlow<ChatScreenState> = _state.asStateFlow()

    /**
     * Retrieves chat history for the current user.
     *
     * Loads all previous messages for the authenticated user from the repository
     * and updates the UI state accordingly.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getChatHistory() {
        userRepository.getUser()?.googleId?.let { loadChatHistory(userId = it) }
    }

    /**
     * Sends a new message from the user to the chat.
     *
     * Adds the message to the local state immediately, then sends it to the repository
     * and processes the AI response when received.
     *
     * @param content The text content of the message to send.
     */
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

    /**
     * Clears the entire chat history for the current user.
     *
     * Removes all messages from both local state and remote storage through
     * the repository.
     */
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadChatHistory(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(userId = userId, isLoading = true) }

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
                                },
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    error.printStackTrace()
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load chat history"
                        )
                    }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseTimestamp(timestamp: String): Long {
        return Instant.parse(timestamp).toEpochMilli()
    }

    internal companion object {
        private const val MESSAGE_STREAM_DELAY = 20L
        private const val SYSTEM = "system"
        private const val USER = "user"
    }
}
