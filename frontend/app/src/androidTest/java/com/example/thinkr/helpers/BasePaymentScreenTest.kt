package com.example.thinkr.helpers

import androidx.compose.ui.test.junit4.createComposeRule
import com.example.thinkr.data.repositories.subscription.SubscriptionRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.payment.PaymentScreen
import com.example.thinkr.ui.payment.PaymentViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule

internal open class BasePaymentScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    protected fun setUpPaymentScreenSubscribeSuccess() {
        val subscriptionRepository = mockk<SubscriptionRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val paymentViewModel = PaymentViewModel(
            subscriptionRepository = subscriptionRepository,
            userRepository = userRepository
        )
        val navToProfile: () -> Unit = {}

        coEvery {
            subscriptionRepository.subscribe(any())
        } returns Result.success(mockk(relaxed = true))

        composeTestRule.setContent {
            PaymentScreen(
                paymentViewModel = paymentViewModel,
                navToProfile = navToProfile
            )
        }
    }

    protected fun setUpPaymentScreenSubscribeFail() {
        val subscriptionRepository = mockk<SubscriptionRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val paymentViewModel = PaymentViewModel(
            subscriptionRepository = subscriptionRepository,
            userRepository = userRepository
        )
        val navToProfile: () -> Unit = {}

        coEvery {
            subscriptionRepository.subscribe(any())
        } returns Result.failure(Exception())

        composeTestRule.setContent {
            PaymentScreen(
                paymentViewModel = paymentViewModel,
                navToProfile = navToProfile
            )
        }
    }
}
