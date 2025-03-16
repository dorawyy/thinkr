package com.example.thinkr.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that manages user profile data and state for the profile screen.
 *
 * @property userRepository Repository used to access and manage user data.
 */
class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileScreenState())
    val state = _state.asStateFlow()

    /**
     * Checks if the current user has a premium subscription.
     *
     * @return Boolean indicating whether the user has premium status.
     */
    fun isPremium(): Boolean {
        return userRepository.getUser()?.subscribed ?: false
    }

    /**
     * Retrieves the current user's profile information and updates the state.
     * Fetches username and email from the user repository.
     */
    fun getProfile() {
        viewModelScope.launch {
            updateProfileInfo(
                username = userRepository.getUser()?.name ?: "Invalid name",
                email = userRepository.getUser()?.email ?: "Invalid email"
            )
        }
    }

    private fun updateProfileInfo(
        username: String,
        email: String
    ) {
        _state.update { it.copy(username = username, email = email) }
    }
}
