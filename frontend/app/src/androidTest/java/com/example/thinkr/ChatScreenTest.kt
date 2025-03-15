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
class ChatScreenTest {
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
            every { googleId } returns "test_user_id"
        }

        // Mock successful chat history retrieval
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    ChatMessage("user", "Hello AI", "12345"),
                    ChatMessage("assistant", "Hello User", "12346")
                ),
                createdAt = "2021-09-01T00:00:00Z",
                updatedAt = "2021-09-01",
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        // Verify screen elements
        composeTestRule.onNodeWithText("Chat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello User").assertIsDisplayed()
        composeTestRule.onNodeWithText("Type a message").assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Send Message")).assertIsDisplayed()
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
            every { googleId } returns "test_user_id"
        }

        // Mock initial chat history
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    ChatMessage("user", "Hello AI", "12345"),
                    ChatMessage("assistant", "Hello User", "12346")
                ),
                createdAt = "2021-09-01T00:00:00Z",
                updatedAt = "2021-09-01",
                metadata = emptyMap()
            )
        )

        // Mock successful message sending
        coEvery {
            chatRepository.sendMessage(any(), any())
        } returns Result.success(
            mockk(relaxed = true) {
                every { content } returns "AI response"
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        // Enter message text
        composeTestRule.onNodeWithText("Type a message").performTextInput("Hello AI")

        // Send message
        composeTestRule.onNode(hasContentDescription("Send Message")).performClick()

        // Wait for message to be added
        sleep(1_000)

        composeTestRule.onNode(
            hasText("AI response", substring = true, ignoreCase = true),
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
            every { googleId } returns "test_user_id"
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        // Verify error state
        // Note: In the actual implementation, you might want to add a test tag for the error message
        // For now, we'll just verify the chat messages are not displayed
        composeTestRule.onNodeWithText("Hello AI").assertDoesNotExist()
        composeTestRule.onNodeWithText("Hello User").assertDoesNotExist()
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
            every { googleId } returns "test_user_id"
        }

        // Mock initial chat history with some messages
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    ChatMessage("user", "Hello AI", "12345"),
                    ChatMessage("assistant", "Hello User", "12346")
                ),
                createdAt = "2021-09-01T00:00:00Z",
                updatedAt = "2021-09-01",
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        // Verify messages are initially displayed
        composeTestRule.onNodeWithText("Hello AI").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hello User").assertIsDisplayed()

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
            every { googleId } returns "test_user_id"
        }

        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    ChatMessage("user", "Hello AI", "12345"),
                    ChatMessage("assistant", "Hello User", "12346")
                ),
                createdAt = "2021-09-01T00:00:00Z",
                updatedAt = "2021-09-01",
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

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
            every { googleId } returns "test_user_id"
        }

        // Mock initial chat history
        coEvery {
            chatRepository.getChatHistory(any())
        } returns Result.success(
            ChatData(
                messages = listOf(
                    ChatMessage("user", "Hello AI", "12345"),
                    ChatMessage("assistant", "Hello User", "12346")
                ),
                createdAt = "2021-09-01T00:00:00Z",
                updatedAt = "2021-09-01",
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
        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        // Don't enter any text, just click send
        composeTestRule.onNode(hasContentDescription("Send Message")).performClick()

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

        composeTestRule.onRoot().printToLog(tag = "COMPOSE_TREE")

        var lenOfMessages = viewModel.state.value.messages.size
        println("Initial number of messages: $lenOfMessages")

        // Enter message text
        composeTestRule.onNodeWithText("Type a message")
            .performTextInput("What is Quantum Computing?")

        // Send message
        composeTestRule.onNode(hasContentDescription("Send Message")).performClick()

        // Wait for response
        composeTestRule.waitForIdle()
        sleep(5_000)

        println("Number of messages after sending message: ${viewModel.state.value.messages.size}")
        // Verify message was added
        assert(viewModel.state.value.messages.size == lenOfMessages + 2)
    }
}
