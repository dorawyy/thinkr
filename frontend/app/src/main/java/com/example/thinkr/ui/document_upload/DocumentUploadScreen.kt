package com.example.thinkr.ui.document_upload

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.thinkr.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Composable that displays the document upload screen of the application.
 *
 * This screen allows users to upload a document with metadata including a name and context.
 * It provides input validation for text fields, ensuring they don't exceed maximum length limits.
 * The screen includes a back navigation button, fields for document metadata, and an upload button.
 *
 * @param navController Navigation controller to handle screen navigation.
 * @param selectedUri The URI of the document file that was selected for upload.
 * @param viewModel ViewModel that manages the document upload process and screen state.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(
    navController: NavController,
    selectedUri: Uri,
    viewModel: DocumentUploadViewModel = koinViewModel()
) {
    var documentName by remember { mutableStateOf("") }
    var documentContext by remember { mutableStateOf("") }
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopAppBar(
            title = { Text("Upload Document") },
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

        Spacer(modifier = Modifier.height(80.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.document_placeholder_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = documentName,
            onValueChange = {
                if (it.length <= DocumentUploadViewModel.MAX_NAME_LENGTH) documentName = it
            },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(0.8f),
            singleLine = true,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = documentContext,
            onValueChange = {
                if (it.length <= DocumentUploadViewModel.MAX_CONTEXT_LENGTH) documentContext = it
            },
            label = { Text("Context") },
            modifier = Modifier
                .fillMaxWidth(0.8f) // Context box is now ~40% of the screen width
                .height(240.dp), // Increased default height
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (documentName.isBlank()) {
                    Toast.makeText(
                        localContext,
                        "Please fill in the name",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    coroutineScope.launch {
                        viewModel.onUpload(
                            navController = navController,
                            documentName = documentName,
                            documentContext = documentContext,
                            uri = selectedUri,
                            context = localContext
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp)
        ) {
            Text("Upload")
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
