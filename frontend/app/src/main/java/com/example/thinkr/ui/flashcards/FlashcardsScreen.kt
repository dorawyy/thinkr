package com.example.thinkr.ui.flashcards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.FlashcardSuggestion
import com.example.thinkr.ui.shared.AnimatedCardDeck
import org.koin.androidx.compose.koinViewModel

/**
 * Composable that displays the flashcards screen of the application.
 *
 * This screen presents flashcards to the user as an interactive card deck that can be swiped
 * through. Users can flip cards horizontally to see the front and back of each flashcard,
 * and swipe vertically to navigate between different flashcards.
 *
 * The screen can load flashcards either from a specific document or from suggested flashcards
 * passed from another screen.
 *
 * @param document The document containing flashcards to be displayed, or null if using suggested flashcards.
 * @param suggestedFlashcards Pre-generated flashcard suggestions to display, or null if loading from a document.
 * @param navController Navigation controller to handle screen navigation.
 * @param viewModel ViewModel that manages the flashcards screen state and operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    document: Document? = null,
    suggestedFlashcards: FlashcardSuggestion? = null,
    navController: NavController,
    viewModel: FlashcardsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (document != null) {
            viewModel.loadFlashcards(document)
        } else if (suggestedFlashcards != null) {
            viewModel.loadSuggestedFlashcards(suggestedFlashcards.flashcards)
        }
    }

    if (state.flashcards.isNotEmpty()) {
        val frontBackPairs: List<Pair<@Composable () -> Unit, @Composable () -> Unit>> = remember {
            state.flashcards.map { flashcard ->
                Pair(
                    // First composable function (front)
                    {
                        Text(
                            text = flashcard.front,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    // Second composable function (back)
                    {
                        Text(
                            text = flashcard.back,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Flashcards") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.onBackPressed(navController) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
            Text(
                text = "Swipe vertically to go through cards.",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Swipe horizontally on the card to flip it.",
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedCardDeck(
                    frontBackPairs = frontBackPairs,
                    enableHorizontalSwipe = true
                )
            }
        }
    }
}
