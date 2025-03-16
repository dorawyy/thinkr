package com.example.thinkr

import android.os.SystemClock.sleep
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thinkr.data.models.ChatData
import com.example.thinkr.data.models.ChatMessage
import com.example.thinkr.data.models.User
import com.example.thinkr.data.remote.chat.ChatApi
import com.example.thinkr.data.repositories.chat.ChatRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.chat.ChatScreen
import com.example.thinkr.ui.chat.ChatViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ChatScreenTest {
    companion object {
        const val TAG = "COMPOSE_TREE"
        const val TEST_USER_ID = "test_user_id"
        const val TEST_CHAT_CREATE_DATE = "2021-09-01T00:00:00Z"
        const val TEST_CHAT_UPDATE_DATE = "2021-09-01"
        const val TEST_MESSAGE_HELLO_AI = "Hello AI"
        const val TEST_MESSAGE_AI_RESPONSE = "AI response"
        const val TEST_MESSAGE_HELLO_USER = "Hello User"
        val TEST_CHAT_MESSAGE_USER = ChatMessage("user", TEST_MESSAGE_HELLO_AI, TEST_CHAT_CREATE_DATE)
        val TEST_CHAT_MESSAGE_ASSISTANT = ChatMessage("assistant", TEST_MESSAGE_HELLO_USER, TEST_CHAT_CREATE_DATE)
        const val SEND_MESSAGE = "Send Message"
        const val MESSAGE_BOX = "Type a message"
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun chatScreen_loadingMessages_displaysCorrectly() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        // Mock successful chat history retrieval
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    TEST_CHAT_MESSAGE_USER,
                    TEST_CHAT_MESSAGE_ASSISTANT
                ),
                createdAt = TEST_CHAT_CREATE_DATE,
                updatedAt = TEST_CHAT_UPDATE_DATE,
                metadata = emptyMap()
            )
        )

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Verify screen elements
        composeTestRule.onNodeWithText("Chat").assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_AI).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_USER).assertIsDisplayed()
        composeTestRule.onNodeWithText(MESSAGE_BOX).assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription(SEND_MESSAGE)).assertIsDisplayed()
    }

    @Test
    fun chatScreen_sendMessage_addsToList() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        // Mock initial chat history
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    TEST_CHAT_MESSAGE_USER,
                    TEST_CHAT_MESSAGE_ASSISTANT
                ),
                createdAt = TEST_CHAT_CREATE_DATE,
                updatedAt = TEST_CHAT_UPDATE_DATE,
                metadata = emptyMap()
            )
        )

        // Mock successful message sending
        coEvery {
            chatRepository.sendMessage(any(), any())
        } returns Result.success(
            mockk(relaxed = true) {
                every { content } returns TEST_MESSAGE_AI_RESPONSE
            }
        )

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Enter message text
        composeTestRule.onNodeWithText(MESSAGE_BOX).performTextInput(TEST_MESSAGE_HELLO_AI)

        // Send message
        composeTestRule.onNode(hasContentDescription(SEND_MESSAGE)).performClick()

        // Wait for message to be added
        sleep(1_000)

        composeTestRule.onNode(
            hasText(TEST_MESSAGE_AI_RESPONSE, substring = true, ignoreCase = true),
            useUnmergedTree = true
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun chatScreen_loadMessagesFails_displaysError() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        // Mock failed chat history retrieval
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.failure(Exception("Network error"))

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Verify error state
        // Note: In the actual implementation, you might want to add a test tag for the error message
        // For now, we'll just verify the chat messages are not displayed
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_AI).assertDoesNotExist()
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_USER).assertDoesNotExist()
    }

    @Test
    fun chatScreen_clearChatHistory_clearsMessages() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        // Mock initial chat history with some messages
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    TEST_CHAT_MESSAGE_USER,
                    TEST_CHAT_MESSAGE_ASSISTANT
                ),
                createdAt = TEST_CHAT_CREATE_DATE,
                updatedAt = TEST_CHAT_UPDATE_DATE,
                metadata = emptyMap()
            )
        )

        // Mock successful clearing of chat history
        coEvery {
            chatRepository.clearChatHistory(any())
        } returns Result.success("Chat history cleared successfully")

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Verify messages are initially displayed
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_AI).assertIsDisplayed()
        composeTestRule.onNodeWithText(TEST_MESSAGE_HELLO_USER).assertIsDisplayed()

        // Clear chat history
        composeTestRule.onNodeWithText("Delete chat").performClick()

        // Verify navigation back happened
        verify { navController.popBackStack() }
    }

    @Test
    fun chatScreen_backButtonPressed_navigatesBack() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    TEST_CHAT_MESSAGE_USER,
                    TEST_CHAT_MESSAGE_ASSISTANT
                ),
                createdAt = TEST_CHAT_CREATE_DATE,
                updatedAt = TEST_CHAT_UPDATE_DATE,
                metadata = emptyMap()
            )
        )

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Click back button
        composeTestRule.onNode(hasContentDescription("Back")).performClick()

        composeTestRule.waitForIdle()

        // Verify navigation back happened
        verify { navController.popBackStack() }
    }

    @Test
    fun chatScreen_emptyMessage_doesNotSend() {
        // Setup mocks
        val chatRepository = mockk<ChatRepository>(relaxed = true)
        val userRepository = mockk<UserRepository>(relaxed = true)
        val navController = mockk<NavController>(relaxed = true)
        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Mock user repository to return a valid user
        every { userRepository.getUser() } returns mockk(relaxed = true) {
            every { googleId } returns TEST_USER_ID
        }

        // Mock initial chat history
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    TEST_CHAT_MESSAGE_USER,
                    TEST_CHAT_MESSAGE_ASSISTANT
                ),
                createdAt = TEST_CHAT_CREATE_DATE,
                updatedAt = TEST_CHAT_UPDATE_DATE,
                metadata = emptyMap()
            )
        )

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Debug the composition tree
        composeTestRule.waitForIdle()
        composeTestRule.onRoot().printToLog(tag = TAG)

        // Don't enter any text, just click send
        composeTestRule.onNode(hasContentDescription(SEND_MESSAGE)).performClick()

        // Verify no message was sent (no repository call)
        verify(exactly = 0) { runTest { chatRepository.sendMessage(any(), any()) } }
    }

    /**
     * End-to-end test for the chat screen
     * No mocking, real network calls
     */
    @Test
    fun chatScreen_e2e() {
        val remoteApi = ChatApi(HttpClient {
            install(ContentNegotiation) {
                json()
            }
        })
        val userRepository = UserRepository()
        userRepository.setUser(
            User(
                email = "test_user@gmail.com",
                name = "Test User",
                googleId = "112119816049214759635",
                subscribed = true
            )
        )
        val chatRepository = ChatRepository(remoteApi)

        val navController = mockk<NavController>(relaxed = true)

        val viewModel = ChatViewModel(
            chatRepository = chatRepository,
            userRepository = userRepository
        )

        // Setup content
        composeTestRule.setContent {
            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // Wait for chat history to load
        composeTestRule.waitForIdle()
        sleep(5_000)

        composeTestRule.onRoot().printToLog(tag = TAG)

        val lenOfMessages = viewModel.state.value.messages.size
        println("Initial number of messages: $lenOfMessages")

        // Enter message text
        composeTestRule.onNodeWithText(MESSAGE_BOX)
            .performTextInput("What is Quantum Computing?")

        // Send message
        composeTestRule.onNode(hasContentDescription(SEND_MESSAGE)).performClick()

        // Wait for response
        composeTestRule.waitForIdle()
        sleep(5_000)

        println("Number of messages after sending message: ${viewModel.state.value.messages.size}")
        // Verify message was added
        assert(viewModel.state.value.messages.size == lenOfMessages + 2)
    }
}
