package com.example.thinkr.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.subscription.SubscriptionRepository
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel that manages the payment and subscription process.
 *
 * @property subscriptionRepository Repository for handling subscription-related API calls.
 * @property userRepository Repository for accessing and updating user data.
 */
class PaymentViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentScreenState())
    val state = _state.asStateFlow()

    /**
     * Initiates the user subscription process.
     *
     * Makes an API call to subscribe the current user, updates the local state,
     * and persists the subscription status in the user repository.
     *
     * @param onSuccess Callback function to be invoked when subscription is successful.
     */
    fun subscribeUser(onSuccess: () -> Unit) {
        val userId = userRepository.getUser()?.googleId ?: return
        viewModelScope.launch {
            val result = subscriptionRepository.subscribe(userId)
            result.onSuccess { response ->
                _state.update { it.copy(isSubscribed = response.data.subscribed) }
                userRepository.subscribeUser()
                onSuccess()
            }.onFailure { error ->
                _state.update { it.copy(errorMessage = "Error: please try again") }
                error.printStackTrace()
            }
        }
    }
}
