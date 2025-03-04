package com.example.thinkr.ui.profile

import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    fun updateProfileInfo(
        username: String,
        email: String
    ) {
        _state.update { it.copy(username = username, email = email) }
    }

    fun isPremium(): Boolean {
        return userRepository.getUser()?.subscribed ?: false
    }

    fun getProfile() {
        viewModelScope.launch {
            updateProfileInfo(
                username = userRepository.getUser()?.name ?: "Invalid name",
                email = userRepository.getUser()?.email ?: "Invalid email"
            )
        }
    }
}
