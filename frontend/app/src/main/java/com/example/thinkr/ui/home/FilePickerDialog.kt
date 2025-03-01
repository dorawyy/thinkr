package com.example.thinkr.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("Recycle")
@Composable
fun FilePickerDialog(
    onDismiss: () -> Unit = {},
    onSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("Could not open document")
                val file = File(context.filesDir, uri.lastPathSegment ?: "unknown")

                inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                selectedFileUri = Uri.fromFile(file)
                selectedFileName = Uri.fromFile(file)?.let { getFileName(context, it) }
            }
        }
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Select a File", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { filePickerLauncher.launch(arrayOf("*/*")) }) {
                        Text(text = "Choose File")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    selectedFileUri?.let {
                        Text(text = "File Name: ${selectedFileName ?: "Unknown"}")
                        Text(text = "URI: $it")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }

    if (selectedFileUri != null) {
        onDismiss()
        onSelected(selectedFileUri!!)
    }
}
