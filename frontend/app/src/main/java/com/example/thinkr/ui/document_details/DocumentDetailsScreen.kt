package com.example.thinkr.ui.document_details

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.thinkr.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.IOException

@Composable
fun DocumentDetailsScreen(
    navController: NavController,
    selectedUri: Uri,
    viewModel: DocumentDetailsViewModel = koinViewModel()
) {
    var documentName by remember { mutableStateOf(value = "") }
    var documentContext by remember { mutableStateOf(value = "") }
    val localContext = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "Back button",
            modifier = Modifier
                .size(48.dp)
                .clickable { viewModel.onBackPressed(navController) }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Selected File: ${selectedUri.lastPathSegment}",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = documentName,
                onValueChange = {
                    if (it.length <= DocumentDetailsViewModel.MAX_NAME_LENGTH) documentName = it
                },
                label = { Text(text = "Name") },
                modifier = Modifier.fillMaxWidth(fraction = 0.8f),
                singleLine = true,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = documentContext,
                onValueChange = {
                    if (it.length <= DocumentDetailsViewModel.MAX_CONTEXT_LENGTH) documentContext = it
                },
                label = { Text(text = "Context") },
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .height(240.dp),
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
                    .fillMaxWidth(fraction = 0.6f)
                    .height(50.dp)
            ) {
                Text(text = "Upload")
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}
