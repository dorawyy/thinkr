package com.example.thinkr

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.ui.profile.ProfileScreen
import com.example.thinkr.ui.profile.ProfileScreenState
import com.example.thinkr.ui.profile.ProfileViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun profileScreen_premiumUser_showsPremiumText() {
        val profileViewModel = mockk<ProfileViewModel>(relaxed = true)
        val mockStateFlow = mockk<StateFlow<ProfileScreenState>>(relaxed = true)

        // Define the value that should be returned when the flow is collected
        val profileState = ProfileScreenState(
            username = "Test User",
            email = "testuser@example.com",
            isPremium = true
        )

        every { mockStateFlow.value } returns profileState
        every { profileViewModel.state } returns mockStateFlow

        composeTestRule.setContent {
            ProfileScreen(
                profileViewModel = profileViewModel,
                isSubscribed = true,
                onPressBack = {},
                onSelectPremium = {}
            )
        }

        composeTestRule
            .onNodeWithText(text = "Username")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Test User")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Email")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "testuser@example.com")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Subscription Plan")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Premium")
            .assertIsDisplayed()
    }

    @Test
    fun profileScreen_notPremiumUser_subscribesToPremium() {
        // TODO
    }
}
