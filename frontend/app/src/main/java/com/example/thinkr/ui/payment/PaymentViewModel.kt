package com.example.thinkr.ui.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thinkr.data.repositories.subscription.SubscriptionRepository
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PaymentScreenState())
    val state = _state.asStateFlow()

    fun onCardNumberChange(cardNumber: String) {
        _state.update { it.copy(cardNumber = cardNumber) }
    }

    fun onExpirationChange(cardExpiration: String) {
        _state.update { it.copy(cardExpiration = cardExpiration) }
    }

    fun onCvcChange(cardCvc: String) {
        _state.update { it.copy(cardCvc = cardCvc) }
    }

    fun onBillingAddressChange(cardBillingAddress: String) {
        _state.update { it.copy(cardBillingAddress = cardBillingAddress) }
    }

    fun subscribeUser() {
        val userId = userRepository.getUser()?.googleId ?: return
        viewModelScope.launch {
            val result = subscriptionRepository.subscribe(userId)
            result.onSuccess { response ->
                _state.update { it.copy(isSubscribed = response.data.subscribed) }
                userRepository.subscribeUser()
            }.onFailure { error ->
                error.printStackTrace()
            }
        }
    }

    fun getSubscriptionStatus(userId: String) {
        viewModelScope.launch {
            val result = subscriptionRepository.getSubscriptionStatus(userId)
            result.onSuccess { response ->
                _state.update { it.copy(isSubscribed = response.data.subscribed) }
            }.onFailure { error ->
                error.printStackTrace()
            }
        }
    }
}
