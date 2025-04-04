package com.example.thinkr.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.ui.shared.GradientButton
import com.example.thinkr.ui.shared.ListItem

/**
 * Composable that displays the home screen of the application.
 *
 * The home screen shows the user's documents, provides document upload functionality,
 * and displays suggested learning materials based on document similarity.
 * It also includes options for signing out and navigating to the profile screen.
 *
 * @param navController Navigation controller to handle screen navigation.
 * @param viewModel ViewModel that manages the home screen state and operations.
 * @param onSignOut Callback function to be invoked when the user signs out.
 */
@SuppressLint("ContextCastToActivity")
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current as? Activity

    BackHandler {
        context?.finish() // Exits the app
    }

    val state = viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        viewModel.getDocuments()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Sign Out") },
            text = { Text(text = "Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; onSignOut() }) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "No")
                }
            }
        )
    }

    HomeScreenContent(
        state = state,
        navController = navController,
        onAction = { action -> viewModel.onAction(action, navController) },
        onSignOut = { showDialog = true },
        viewModel
    )
}

@Composable
private fun HomeScreenContent(
    state: State<HomeScreenState>,
    navController: NavController,
    onAction: (HomeScreenAction) -> Unit,
    onSignOut: () -> Unit,
    viewModel: HomeViewModel
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.value.showDialog) {
            FilePickerDialog(
                onDismiss = { onAction(HomeScreenAction.DismissDialog) },
                onSelected = { onAction(HomeScreenAction.FileSelected(it)) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { onSignOut(); viewModel.signOut() }) {
                    Text(text = "Sign out")
                }
                TextButton(onClick = { onAction(HomeScreenAction.ProfileButtonClicked) }) {
                    Text(text = "Profile")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(state.value.retrievedDocuments) { document ->
                    ListItem(document, onAction)
                }

                items(state.value.uploadingDocuments) { item ->
                    ListItem(item, onAction)
                }

                item {
                    TextButton(
                        onClick = { onAction(HomeScreenAction.AddButtonClicked) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(text = "Add")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "View Suggested Materials",
                        gradientColors = if (state.value.retrievedDocuments.isEmpty()) listOf(
                            Color(
                                0xFF9E9E9E
                            ), Color(0xFF9E9E9E)
                        ) else listOf(Color(0xFF00CCCC), Color(0xFF3399FF)),
                        modifier = Modifier.align(Alignment.CenterHorizontally).width(400.dp)
                            .clickable { state.value.retrievedDocuments.isEmpty() },
                        onClick = { navController.navigate(Route.SuggestedMaterials) },
                    )
                }
            }
        }
    }
}
