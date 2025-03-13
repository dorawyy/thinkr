package com.example.thinkr.helpers

import androidx.compose.ui.test.junit4.createComposeRule
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.example.thinkr.ui.landing.LandingScreen
import com.example.thinkr.ui.landing.LandingScreenViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import kotlin.properties.Delegates

open class BaseLandingScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    protected var navigateToHomeCalled by Delegates.notNull<Boolean>()

    @Before
    fun setUp() {
        navigateToHomeCalled = false
    }

    protected fun setUpLandingScreenLoginSuccess() {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val authRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = LandingScreenViewModel(
            userRepository = userRepository,
            authRepository = authRepository
        )

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
    }

    protected fun setUpLandingScreenLoginFail() {
        val userRepository = mockk<UserRepository>(relaxed = true)
        val authRepository = mockk<AuthRepository>(relaxed = true)
        val viewModel = LandingScreenViewModel(
            userRepository = userRepository,
            authRepository = authRepository
        )

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
    }
}
