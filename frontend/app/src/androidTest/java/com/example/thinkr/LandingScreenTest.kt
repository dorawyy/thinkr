package com.example.thinkr

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.helpers.BaseLandingScreenTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LandingScreenTest : BaseLandingScreenTest() {
    @Test
    fun landingScreen_loginSuccess() {
        setUpLandingScreenLoginSuccess()

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
        setUpLandingScreenLoginFail()

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
