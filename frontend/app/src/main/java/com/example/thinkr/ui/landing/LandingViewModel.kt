package com.example.thinkr.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the landing screen state and authentication processes.
 *
 * Responsible for handling Google Sign-In authentication flow, verifying user sessions,
 * and updating the UI state accordingly.
 *
 * @property authRepository Repository for handling authentication-related API calls.
 * @property userRepository Repository for accessing and updating user data.
 */
class LandingViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(LandingScreenState())
    val state = _state.asStateFlow()

    /**
     * Processes the Google Sign-In authentication result.
     *
     * Validates the account information and attempts to authenticate with the backend server.
     * Updates UI state based on authentication success or failure.
     *
     * @param account The Google Sign-In account information.
     * @param onSignOut Callback function to be invoked when sign-out is required.
     */
    fun onGoogleSignInResult(
        account: GoogleSignInAccount,
        onSignOut: () -> Unit
    ) {
        if (account.id == null) {
            _state.update {
                it.copy(
                    error = "Sign in failed for ${account.email} since Google UUID is ${account.id}"
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authRepository.login(
                googleId = account.id!!,
                name = account.displayName ?: "",
                email = account.email ?: ""
            ).fold(
                onSuccess = {
                    userRepository.setUser(it.data.user.copy())
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isAuthenticated = true
                        )
                    }
                },
                onFailure = { exception ->
                    userRepository.delUser()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message
                        )
                    }
                    onSignOut()
                }
            )
        }
    }

    /**
     * Checks if the user is already signed in.
     *
     * If a valid user session exists, updates the UI state and navigates to the home screen.
     *
     * @param navigateToHome Callback function to navigate to the home screen.
     */
    fun checkSignedIn(navigateToHome: () -> Unit) {
        if (userRepository.getUser() != null) {
            _state.update { it.copy(isAuthenticated = true) }
            navigateToHome()
        }
    }

    /**
     * Determines if the user has explicitly signed out.
     *
     * @return Boolean indicating whether the user has signed out.
     */
    fun userSignedOut(): Boolean {
        return userRepository.isSignedOut()
    }
}
