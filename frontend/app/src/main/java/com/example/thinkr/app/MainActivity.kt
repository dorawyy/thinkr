package com.example.thinkr.app

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardSuggestion
import com.example.thinkr.data.models.QuizSuggestion
import com.example.thinkr.ui.chat.ChatScreen
import com.example.thinkr.ui.chat.ChatViewModel
import com.example.thinkr.ui.document_options.DocumentOptionsScreen
import com.example.thinkr.ui.document_options.DocumentOptionsViewModel
import com.example.thinkr.ui.document_upload.DocumentUploadScreen
import com.example.thinkr.ui.document_upload.DocumentUploadViewModel
import com.example.thinkr.ui.flashcards.FlashcardsScreen
import com.example.thinkr.ui.flashcards.FlashcardsViewModel
import com.example.thinkr.ui.home.HomeScreen
import com.example.thinkr.ui.home.HomeViewModel
import com.example.thinkr.ui.landing.LandingScreen
import com.example.thinkr.ui.landing.LandingViewModel
import com.example.thinkr.ui.payment.PaymentScreen
import com.example.thinkr.ui.payment.PaymentViewModel
import com.example.thinkr.ui.profile.ProfileScreen
import com.example.thinkr.ui.profile.ProfileViewModel
import com.example.thinkr.ui.quiz.QuizScreen
import com.example.thinkr.ui.quiz.QuizViewModel
import com.example.thinkr.ui.suggested_materials.SuggestedMaterialsScreen
import com.example.thinkr.ui.suggested_materials.SuggestedMaterialsViewModel
import com.example.thinkr.ui.theme.ThinkrTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.koinViewModel

/**
 * Main activity for the Thinkr application that handles navigation.
 *
 * This activity sets up the navigation routes for the application.
 * It manages all screen destinations, route parameters, and
 * the Google Sign-In authentication.
 */
class MainActivity : ComponentActivity() {
    lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Called when the activity is first created.
     *
     * Sets up the Google Sign-In, initializes the navigation graph, and defines
     * all application routes with their respective Composable functions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down, this contains the data it most recently supplied.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupGoogleSignIn()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val startDestination = Route.Landing

            ThinkrTheme {
                Column(modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp)) {
                    NavHost(
                        navController = navController,
                        startDestination = Route.RouteGraph
                    ) {
                        navigation<Route.RouteGraph>(startDestination = startDestination) {
                            setupLandingRoute(navController)
                            setupHomeRoute(navController)
                            setupSuggestedMaterialsRoute(navController)
                            setupDocumentOptionsRoute(navController)
                            setupDocumentUploadRoute(navController)
                            setupProfileRoute(navController)
                            setupPaymentRoute(navController)
                            setupFlashcardsRoute(navController)
                            setupQuizRoute(navController)
                            setupChatRoute(navController)
                        }
                    }
                }
            }
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun NavGraphBuilder.setupLandingRoute(navController: NavController) {
        composable<Route.Landing> {
            val viewModel = koinViewModel<LandingViewModel>()

            LandingScreen(
                viewModel = viewModel,
                navigateToHome = { navController.navigate(Route.Home) },
                onSignOut = { googleSignInClient.signOut() }
            )
        }
    }

    private fun NavGraphBuilder.setupHomeRoute(navController: NavController) {
        composable<Route.Home> {
            val viewModel = koinViewModel<HomeViewModel>()

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
    }

    private fun NavGraphBuilder.setupSuggestedMaterialsRoute(navController: NavController) {
        composable<Route.SuggestedMaterials> {
            val viewModel = koinViewModel<SuggestedMaterialsViewModel>()

            SuggestedMaterialsScreen(
                navController = navController,
                viewModel = viewModel,
            )
        }
    }

    private fun NavGraphBuilder.setupDocumentOptionsRoute(navController: NavController) {
        composable(
            route = Route.DocumentOptions.ROUTE,
            arguments = listOf(navArgument(Route.DocumentOptions.ARGUMENT) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString(Route.DocumentOptions.ARGUMENT) ?: ""
            val document = Json.decodeFromString<Document>(Uri.decode(json))
            val viewModel = koinViewModel<DocumentOptionsViewModel>()
            DocumentOptionsScreen(document, navController, viewModel)
        }
    }

    private fun NavGraphBuilder.setupDocumentUploadRoute(navController: NavController) {
        composable(
            route = Route.DocumentUpload.ROUTE,
            arguments = listOf(navArgument(Route.DocumentUpload.ARGUMENT) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString(Route.DocumentUpload.ARGUMENT) ?: ""
            val selectedUri = Uri.decode(json).toUri()
            val viewModel = koinViewModel<DocumentUploadViewModel>()

            DocumentUploadScreen(navController, selectedUri, viewModel)
        }
    }

    private fun NavGraphBuilder.setupProfileRoute(navController: NavController) {
        composable<Route.Profile> { navBackStackEntry ->
            val viewModel = koinViewModel<ProfileViewModel>()
            val paymentViewModel = navBackStackEntry
                .sharedKoinViewModel<PaymentViewModel>(navController)
            val isPremium = paymentViewModel.state.value.isSubscribed

            ProfileScreen(
                profileViewModel = viewModel,
                isSubscribed = isPremium,
                onPressBack = { navController.navigate(Route.Home) },
                onSelectPremium = { navController.navigate(Route.Payment) }
            )
        }
    }

    private fun NavGraphBuilder.setupPaymentRoute(navController: NavController) {
        composable<Route.Payment> { navBackStackEntry ->
            val paymentViewModel = navBackStackEntry
                .sharedKoinViewModel<PaymentViewModel>(navController)

            PaymentScreen(
                paymentViewModel = paymentViewModel,
                navToProfile = { navController.navigate(Route.Profile) }
            )
        }
    }

    private fun NavGraphBuilder.setupFlashcardsRoute(navController: NavController) {
        composable(
            route = Route.Flashcards.ROUTE,
            arguments = listOf(
                navArgument(Route.Flashcards.DOCUMENT_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(Route.Flashcards.FLASHCARD_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val documentJson =
                backStackEntry.arguments?.getString(Route.Flashcards.DOCUMENT_ARGUMENT) ?: ""
            val flashcardJson =
                backStackEntry.arguments?.getString(Route.Flashcards.FLASHCARD_ARGUMENT) ?: ""
            val viewModel = koinViewModel<FlashcardsViewModel>()

            val document = if (documentJson.isNotEmpty()) {
                Json.decodeFromString<Document>(Uri.decode(documentJson))
            } else {
                null
            }

            val flashcardSet = if (flashcardJson.isNotEmpty()) {
                Json.decodeFromString<FlashcardSuggestion>(Uri.decode(flashcardJson))
            } else {
                null
            }

            FlashcardsScreen(
                document = document,
                suggestedFlashcards = flashcardSet,
                navController = navController,
                viewModel = viewModel
            )
        }
    }

    private fun NavGraphBuilder.setupQuizRoute(navController: NavController) {
        composable(
            route = Route.Quiz.ROUTE,
            arguments = listOf(
                navArgument(Route.Quiz.DOCUMENT_ARGUMENT) {
                    type = NavType.StringType
                },
                navArgument(Route.Quiz.QUIZ_ARGUMENT) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val documentJson =
                backStackEntry.arguments?.getString(Route.Quiz.DOCUMENT_ARGUMENT) ?: ""
            val quizJson = backStackEntry.arguments?.getString(Route.Quiz.QUIZ_ARGUMENT) ?: ""
            val viewModel = koinViewModel<QuizViewModel>()

            val document = if (documentJson.isNotEmpty()) {
                Json.decodeFromString<Document>(Uri.decode(documentJson))
            } else {
                null
            }

            val quizSet = if (quizJson.isNotEmpty()) {
                Json.decodeFromString<QuizSuggestion>(Uri.decode(quizJson))
            } else {
                null
            }

            QuizScreen(
                document = document,
                suggestedQuiz = quizSet,
                navController = navController,
                viewModel = viewModel
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun NavGraphBuilder.setupChatRoute(navController: NavController) {
        composable(
            route = Route.Chat.ROUTE,
            arguments = listOf(navArgument(Route.Chat.ARGUMENT) {
                type = NavType.StringType
            })
        ) {
//            val json = backStackEntry.arguments?.getString(Route.Chat.ARGUMENT) ?: ""
//            val document = Json.decodeFromString<Document>(Uri.decode(json))
            val viewModel = koinViewModel<ChatViewModel>()

            ChatScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}