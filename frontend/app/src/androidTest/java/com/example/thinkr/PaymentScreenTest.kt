package com.example.thinkr

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.helpers.BasePaymentScreenTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaymentScreenTest : BasePaymentScreenTest() {
    @Test
    fun paymentScreen_subscribeSuccess() {
        setUpPaymentScreenSubscribeSuccess()

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        composeTestRule
            .onNodeWithText(text = "Start free trial!")
            .performClick()
        composeTestRule
            .onNodeWithText(text = "Error", substring = true)
            .assertIsNotDisplayed()
    }

    @Test
    fun paymentScreen_subscribeFail() {
        setUpPaymentScreenSubscribeFail()

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        composeTestRule
            .onNodeWithText(text = "Start free trial!")
            .performClick()
        composeTestRule
            .onNodeWithText(text = "Error", substring = true)
            .assertIsDisplayed()
    }
}
