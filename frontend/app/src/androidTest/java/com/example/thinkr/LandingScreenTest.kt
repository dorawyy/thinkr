package com.example.thinkr

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.landing.LandingScreen
import com.example.thinkr.ui.landing.LandingScreenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun landingScreen_loginSuccess_navigatesToHome() {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val authRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = LandingScreenViewModel(
            userRepository = userRepository,
            authRepository = authRepository
        )

        var navigateToHomeCalled = false
        val navigateToHome: () -> Unit = { navigateToHomeCalled = true }
        val onSignOut: () -> Unit = {}

        coEvery {
            viewModel.onGoogleSignInResult(
                account = GoogleSignInAccount.createDefault(),
                onSignOut = { any() }
            )
            authRepository.login(
                googleId = any(),
                name = any(),
                email = any()
            )
        } returns Result.success(mockk(relaxed = true))

        composeTestRule.setContent {
            LandingScreen(
                viewModel = viewModel,
                navigateToHome = navigateToHome,
                onSignOut = onSignOut
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        composeTestRule
            .onNode(hasContentDescription(value = "App Logo"))
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Welcome to Thinkr")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(testTag = "google_sign_in_button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "google_sign_in_button")
            .performClick()

        composeTestRule.waitForIdle()

        assertTrue(navigateToHomeCalled)
        composeTestRule
            .onNodeWithTag(testTag = "login_error")
            .assertDoesNotExist()
    }

    @Test
    fun landingScreen_loginFail_showErrorMessage() {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val authRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = LandingScreenViewModel(
            userRepository = userRepository,
            authRepository = authRepository
        )

        var navigateToHomeCalled = false
        val navigateToHome: () -> Unit = {
            navigateToHomeCalled = false
        }
        val onSignOut: () -> Unit = {}

        coEvery {
            viewModel.onGoogleSignInResult(
                account = GoogleSignInAccount.createDefault(),
                onSignOut = { any() }
            )
            authRepository.login(
                googleId = any(),
                name = any(),
                email = any()
            )
        } returns Result.failure(exception = Exception())

        composeTestRule.setContent {
            LandingScreen(
                viewModel = viewModel,
                navigateToHome = navigateToHome,
                onSignOut = onSignOut
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        composeTestRule
            .onNode(hasContentDescription(value = "App Logo"))
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(text = "Welcome to Thinkr")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag(testTag = "google_sign_in_button")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag(testTag = "google_sign_in_button")
            .performClick()

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        assertFalse(navigateToHomeCalled)
        composeTestRule
            .onNodeWithText(text = "Sign in failed", substring = true)
            .assertIsDisplayed()
    }
}
