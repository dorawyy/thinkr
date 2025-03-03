package com.example.thinkr.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thinkr.R
import com.example.thinkr.app.Route
import com.example.thinkr.ui.shared.ListItem

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel,
    onSignOut: () -> Unit
) {
    val state = viewModel.state.collectAsState()
    var showDialog by remember { mutableStateOf(value = false) }

    LaunchedEffect(Unit) {
        viewModel.getDocuments()
        viewModel.getSuggestedMaterial()
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
        onSignOut = { showDialog = true }
    )
}

@Composable
fun HomeScreenContent(
    state: State<HomeScreenState>,
    navController: NavController,
    onAction: (HomeScreenAction) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                navController.navigate(Route.DocumentUpload.createRoute(uri))
            } catch (e: SecurityException) {
                Log.e("HomeScreen", "Failed to get permission", e)
                Toast.makeText(
                    context,
                    "Cannot access this file. Please try another.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

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
                TextButton(onClick = onSignOut) {
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
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (state.value.suggestedMaterials.flashcards.isNotEmpty()) {
                    item {
                        Text(
                            text = "Flashcards",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(state.value.suggestedMaterials.flashcards) { flashcardSet ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // Navigate to flashcard detail screen if needed
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "From document: ${flashcardSet.documentId}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${flashcardSet.flashcards.size} flashcards",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (state.value.suggestedMaterials.quizzes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Quizzes",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(state.value.suggestedMaterials.quizzes) { quizSet ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // Navigate to quiz detail screen if needed
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "From document: ${quizSet.documentId}",
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${quizSet.quiz.size} questions",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun showSettingsDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle("Permission Denied")
        .setMessage("You have denied this permission permanently. Please enable it in settings.")
        .setPositiveButton("Go to Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel", null)
        .show()
}

fun getFileName(context: Context, uri: Uri): String? {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1) {
                return it.getString(nameIndex)
            }
        }
    }
    return null
}
