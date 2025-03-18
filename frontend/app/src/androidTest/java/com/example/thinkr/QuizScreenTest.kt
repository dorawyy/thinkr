package com.example.thinkr

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.swipeUp
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.QuizItem
import com.example.thinkr.data.models.User
import com.example.thinkr.data.remote.study.StudyApi
import com.example.thinkr.data.repositories.quiz.QuizRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.quiz.QuizScreen
import com.example.thinkr.ui.quiz.QuizViewModel
import io.ktor.client.HttpClient
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Thread.sleep

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
internal class QuizScreenTest {
    companion object {
        private const val COMPOSE_TREE = "COMPOSE_TREE"

        // Test data for quiz items
        private val TEST_QUIZ_ITEMS = listOf(
            QuizItem(
                question = "Who theorized that Schrödinger's Constant could be manipulated to transmit structured data without energy loss?",
                options = mapOf(
                    "A" to "Dr. John Smith",
                    "B" to "Dr. Emily Liu",
                    "C" to "Dr. Lena Vasquez",
                    "D" to "Dr. Michael Chang"
                ),
                answer = "C"
            ),
            QuizItem(
                question = "What did the independent team of bioengineers from Osaka, Japan discover in 2094?",
                options = mapOf(
                    "A" to "A new renewable energy source",
                    "B" to "A way to encode memories into synthetic neurons",
                    "C" to "A method to time travel",
                    "D" to "A cure for cancer"
                ),
                answer = "B"
            ),
            QuizItem(
                question = "What was demonstrated when subject #117 used a neuronal patch embedded in the left temporal lobe?",
                options = mapOf(
                    "A" to "Telepathic communication",
                    "B" to "Enhanced physical strength",
                    "C" to "Ability to predict the future",
                    "D" to "Learning fluent Mandarin in 8.2 minutes"
                ),
                answer = "D"
            ),
            QuizItem(
                question = "What was the name of the autonomous AI at Arctic Research Outpost Z-45?",
                options = mapOf(
                    "A" to "NOVA-7",
                    "B" to "AURORA-5",
                    "C" to "SOLARIS-3",
                    "D" to "ORION-9"
                ),
                answer = "A"
            ),
            QuizItem(
                question = "Where was the encrypted transmission from Eris Base located?",
                options = mapOf(
                    "A" to "37.8°N, 122.4°W",
                    "B" to "0°N, 0°E",
                    "C" to "14.5°N, 152.3°E",
                    "D" to "90°S, 0°E"
                ),
                answer = "C"
            )
        )

        private val TEST_DOCUMENT = Document(
            documentId = "test_document6130544703607720127.pdf",
            documentName = "Test Document",
            uploadTime = "2025-03-13 17:49:53",
            activityGenerationComplete = true
        )

        private val TEST_USER = User(
            email = "test_user@gmail.com",
            name = "Test User",
            googleId = "112119816049214759635",
            subscribed = false
        )
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun quizScreen_e2e() {
        val studyApi = StudyApi(HttpClient())
        val userRepository = UserRepository()
        userRepository.setUser(TEST_USER)
        val quizRepository = QuizRepository(studyApi, userRepository)
        val navController = mockk<NavController>(relaxed = true)

        // Create the view model with mocked repository
        val viewModel = QuizViewModel(quizRepository)

        // Set up the QuizScreen
        composeTestRule.setContent {
            QuizScreen(
                document = TEST_DOCUMENT,
                suggestedQuiz = null,
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.onRoot().printToLog(tag = COMPOSE_TREE)

        // Start the quiz
        composeTestRule.onNodeWithText("Start Quiz").performClick()
        composeTestRule.waitForIdle()
        sleep(1_000)

        // Loop through all quiz items
        for (quizItem in TEST_QUIZ_ITEMS) {
            composeTestRule.waitForIdle()
            sleep(1_000)
            composeTestRule.onRoot().printToLog(tag = COMPOSE_TREE)

            // Verify the question is displayed
            println("Question: ${quizItem.question}")
            val questionNode = composeTestRule.onNodeWithText(quizItem.question)
            questionNode.assertIsDisplayed()

            // Verify all options are displayed and click on the correct answer
            for ((key, option) in quizItem.options) {
                println("Option $key: $option")
                val keyNode = composeTestRule.onNodeWithText(key)
                val optionNode = composeTestRule.onNodeWithText(option)
                optionNode.assertIsDisplayed()

                // Click on the correct answer
                if (key == quizItem.answer) {
                    optionNode.performClick()
                    composeTestRule.waitForIdle()
                    sleep(500)
                }
            }

            // Swipe up to go to the next question (unless it's the last one)
            if (quizItem != TEST_QUIZ_ITEMS.last()) {
                composeTestRule.onRoot().performTouchInput {
                    // Swipe around the edges as mentioned in the UI instruction
                    swipeUp(startY = bottom - 50, endY = top + 50)
                }
                composeTestRule.waitForIdle()
                sleep(1_000)
            }
        }

        // Force the timer to expire (call onQuizTimeUp directly)
        viewModel.onQuizTimeUp()
        composeTestRule.waitForIdle()
        sleep(1_000)

        // Check if the score is displayed correctly (should be 3/3 as we selected all correct answers)
        composeTestRule.onNodeWithText("Score: 5 / 5").assertIsDisplayed()
    }
}
