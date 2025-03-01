package com.example.thinkr.ui.document_details

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.repositories.doc.DocRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class DocumentDetailsViewModel(private val docRepository: DocRepository) : ViewModel() {
    private val _state = MutableStateFlow(DocumentDetailsState())

    fun onBackPressed(navController: NavController) {
        navController.navigate(Route.Home)
    }

    fun onUpload(
        navController: NavController,
        documentName: String,
        documentContext: String,
        uri: Uri,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Could not open document")

                inputStream.use { stream ->
                    docRepository.uploadDocument(
                        document = stream,
                        userId = "69",
                        documentName = documentName,
                        documentContext = documentContext
                    )
                }

                navController.navigate(Route.Home)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            }
        }
        _state.update { it.copy(name = documentName, context = documentContext, uri = uri) }
    }

    companion object {
        const val MAX_NAME_LENGTH = 50
        const val MAX_CONTEXT_LENGTH = 500
    }
}
