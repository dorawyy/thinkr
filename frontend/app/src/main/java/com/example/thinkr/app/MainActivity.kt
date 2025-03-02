package com.example.thinkr.app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.thinkr.data.models.Document
import com.example.thinkr.ui.document_options.DocumentOptionsViewModel
import com.example.thinkr.ui.document_options.DocumentOptionsScreen
import com.example.thinkr.ui.document_upload.DocumentUploadScreen
import com.example.thinkr.ui.document_upload.DocumentUploadViewModel
import com.example.thinkr.ui.flashcards.FlashcardsScreen
import com.example.thinkr.ui.flashcards.FlashcardsViewModel
import com.example.thinkr.ui.chat.ChatScreen
import com.example.thinkr.ui.home.HomeScreen
import com.example.thinkr.ui.home.HomeScreenViewModel
import com.example.thinkr.ui.landing.LandingScreen
import com.example.thinkr.ui.landing.LandingScreenViewModel
import com.example.thinkr.ui.payment.PaymentScreen
import com.example.thinkr.ui.payment.PaymentViewModel
import com.example.thinkr.ui.profile.ProfileScreen
import com.example.thinkr.ui.profile.ProfileViewModel
import com.example.thinkr.ui.quiz.QuizScreen
import com.example.thinkr.ui.theme.ThinkrTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val account = remember { GoogleSignIn.getLastSignedInAccount(this) }
            val startDestination = remember { if (account != null) Route.Home else Route.Landing }

            ThinkrTheme {
                Column(modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp)) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.RouteGraph
                    ) {
                        navigation<Route.RouteGraph>(startDestination = startDestination) {
                            composable<Route.Landing> {
                                val viewModel = koinViewModel<LandingScreenViewModel>()

                                LandingScreen(
                                    viewModel = viewModel,
                                    onLogin = { navController.navigate(Route.Home) },
                                    onSignUp = { navController.navigate(Route.Home) },
                                    navigateToHome = { navController.navigate(Route.Home) },
                                    onSignOut = { googleSignInClient.signOut() }
                                )
                            }

                            composable<Route.Home> {
                                val viewModel = koinViewModel<HomeScreenViewModel>()

                                HomeScreen(
                                    navController = navController,
                                    viewModel = viewModel,
                                    onSignOut = {
                                        googleSignInClient.signOut().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                navController.navigate(Route.Landing)
                                            }
                                        }
                                    }
                                )
                            }

                            composable(
                                route = Route.DocumentOptions.ROUTE,
                                arguments = listOf(navArgument(Route.DocumentOptions.ARGUMENT) {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val json =
                                    backStackEntry.arguments?.getString(Route.DocumentOptions.ARGUMENT)
                                        ?: ""
                                val document =
                                    Json.decodeFromString<Document>(Uri.decode(json)) // Decode JSON back to object
                                val viewModel = koinViewModel<DocumentOptionsViewModel>()
                                DocumentOptionsScreen(document, navController, viewModel)
                            }

                            composable(
                                route = Route.DocumentUpload.ROUTE,
                                arguments = listOf(navArgument(Route.DocumentUpload.ARGUMENT) {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val json =
                                    backStackEntry.arguments?.getString(Route.DocumentUpload.ARGUMENT)
                                        ?: ""
                                val selectedUri = Uri.parse(Uri.decode(json))
                                val viewModel = koinViewModel<DocumentUploadViewModel>()

                                DocumentUploadScreen(navController, selectedUri, viewModel)
                            }

                            composable<Route.Profile> {
                                val viewModel = koinViewModel<ProfileViewModel>()

                                ProfileScreen(
                                    viewModel = viewModel,
                                    onPressBack = { navController.navigate(Route.Home) },
                                    onSelectPremium = { navController.navigate(Route.Payment) }
                                )
                            }

                            composable<Route.Payment> {
                                val viewModel = koinViewModel<PaymentViewModel>()

                                PaymentScreen(
                                    viewModel = viewModel,
                                    onConfirm = { navController.navigate(Route.Profile) }
                                )
                            }

                            composable(
                                route = Route.Flashcards.ROUTE,
                                arguments = listOf(navArgument(Route.Flashcards.ARGUMENT) {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val json =
                                    backStackEntry.arguments?.getString(Route.Flashcards.ARGUMENT)
                                        ?: ""
                                val document =
                                    Json.decodeFromString<Document>(Uri.decode(json)) // Decode JSON back to object
                                val viewModel = koinViewModel<FlashcardsViewModel>()

                                FlashcardsScreen(document, navController, viewModel)
                            }

                            composable(
                                route = Route.Quiz.ROUTE,
                                arguments = listOf(navArgument(Route.Quiz.ARGUMENT) {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val json =
                                    backStackEntry.arguments?.getString(Route.Quiz.ARGUMENT)
                                        ?: ""
                                val document =
                                    Json.decodeFromString<Document>(Uri.decode(json)) // Decode JSON back to object

                                QuizScreen(document, navController)
                            }

                            composable(
                                route = Route.Chat.ROUTE,
                                arguments = listOf(navArgument(Route.Chat.ARGUMENT) {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val json =
                                    backStackEntry.arguments?.getString(Route.Chat.ARGUMENT)
                                        ?: ""
                                val document =
                                    Json.decodeFromString<Document>(Uri.decode(json)) // Decode JSON back to object

                                ChatScreen(document, navController)
                            }
                        }
                    }
                }
            }
        }
    }
}