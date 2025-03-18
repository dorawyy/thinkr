package com.example.thinkr.ui.document_options

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.models.Document
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

/**
 * Composable that displays the document options screen, showing available actions for a document.
 *
 * This screen presents various learning options for a document such as quizzes, flashcards, and
 * AI chat. Options appear with staggered animations and are disabled until the document is ready.
 * Premium features like AI chat are handled differently based on subscription status.
 *
 * @param documentItem The document for which options should be displayed.
 * @param navController Navigation controller to handle screen navigation.
 * @param viewModel ViewModel that manages the document options screen state and operations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentOptionsScreen(
    documentItem: Document,
    navController: NavController,
    viewModel: DocumentOptionsViewModel = koinViewModel()
) {
    val localContext = LocalContext.current

    LaunchedEffect(documentItem) {
        viewModel.checkIfDocumentIsReady(documentItem, localContext, navController)
    }

    val state by viewModel.state.collectAsState()

    // Animation states
    var optionsVisible by remember { mutableStateOf(false) }

    // Staggered animation for options
    LaunchedEffect(Unit) {
        optionsVisible = true
    }

    // Title and main content
    TopAppBar(
        title = { Text("Document Options") },
        navigationIcon = {
            IconButton(
                onClick = { navController.navigate(Route.Home) }
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
        if (!state.isReady) {
            Text("We are preparing your study material", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(18.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(32.dp))
        }

        val chatAIOption = if (viewModel.isPremium()) Pair("Chat with Thinkr AI") {
            navController.navigate(Route.Chat.createRoute(documentItem))
        } else Pair("Chat with Thinkr AI: Get Premium!") {
            navController.navigate(Route.Payment)
        }

        // Option buttons with staggered animation
        val options: List<Pair<String, () ->
        Unit>> = listOf(
            Pair("Take Quiz") {
                navController.navigate(Route.Quiz.createRoute(documentItem))
            },
            Pair("Review Flashcards") {
                navController.navigate(Route.Flashcards.createRoute(documentItem))
            },
            chatAIOption
        )

        options.forEachIndexed { index, (title, onClick) ->
            val showDelay = 100L * index

            LaunchedEffect(optionsVisible) {
                if (optionsVisible) {
                    delay(showDelay)
                }
            }

            DocumentOptionButtonWrapper(optionsVisible, title, onClick, state.isReady)
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
private fun DocumentOptionButtonWrapper(
    visible: Boolean,
    title: String,
    onClick: () -> Unit,
    isReady: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(spring(dampingRatio = 0.7f)) +
                expandVertically(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow))
    ) {
        DocumentOptionButton(title = title, onClick = onClick, isReady = isReady)
    }
}

@Composable
private fun DocumentOptionButton(
    title: String,
    onClick: () -> Unit,
    isReady: Boolean
) {
    Button(
        onClick = {
            if (isReady) onClick()
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isReady) MaterialTheme.colorScheme.primary else Color.LightGray,
            contentColor = if (isReady) Color.LightGray else Color.Gray
        ),
        shape = RoundedCornerShape(28.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}
