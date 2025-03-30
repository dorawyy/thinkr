package com.example.thinkr.ui.suggested_materials

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Composable function that displays the Suggested Materials screen.
 *
 * This screen shows a list of suggested materials such as flashcards and quizzes
 * based on the most similar document. It also handles the loading state and navigation.
 *
 * @param navController The navigation controller to handle screen transitions.
 * @param viewModel The ViewModel that provides the state and handles the business logic.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedMaterialsScreen(navController: NavController, viewModel: SuggestedMaterialsViewModel) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getSuggestedMaterial()
    }

    TopAppBar(
        title = { Text("Suggested Materials") },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Materials from a most similar document",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.suggestedMaterials.flashcards.isNotEmpty()) {
                    item {
                        Text(text = "Flashcards")
                    }

                    items(state.suggestedMaterials.flashcards) { flashcardSet ->
                        Text(text = "Document Name: ${flashcardSet.documentName}")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val flashcardJson = Json.encodeToString(flashcardSet)
                                    navController.navigate(
                                        Route.Flashcards.createRoute(
                                            flashcardSuggestion = flashcardJson
                                        )
                                    )
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${flashcardSet.flashcards.size} flashcards",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(text = "No similar document with flashcards")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.suggestedMaterials.quizzes.isNotEmpty()) {
                    item {
                        Text(text = "Quiz")
                    }

                    items(state.suggestedMaterials.quizzes) { quizSet ->
                        Text(text = "Document Name: ${quizSet.documentName}")
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val quizJson = Json.encodeToString(quizSet)
                                    navController.navigate(Route.Quiz.createRoute(quizSuggestion = quizJson))
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${quizSet.quiz.size} questions",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(text = "No similar document with quiz")
                    }
                }
            }
        }
    }
}
