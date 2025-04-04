package com.example.thinkr.ui.document_upload

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.IOException

/**
 * ViewModel that manages the document upload process and screen state.
 *
 * Handles uploading documents to the repository, input validation,
 * and navigation flows related to the document upload screen.
 *
 * @property docRepository Repository for uploading and managing documents.
 * @property userRepository Repository for accessing current user information.
 */
class DocumentUploadViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentUploadScreenState())

    /**
     * Navigates back to the home screen.
     *
     * @param navController Navigation controller to handle screen transition.
     */
    fun onBackPressed(navController: NavController) {
        navController.navigate(Route.Home)
    }

    /**
     * Uploads a document to the repository.
     *
     * Reads the selected file from the URI, uploads it to the repository with the provided
     * metadata, and handles navigation after successful upload. Displays appropriate error
     * messages on failure.
     *
     * @param navController Navigation controller to handle screen transition after upload.
     * @param documentName Name of the document being uploaded.
     * @param documentContext Additional context information about the document.
     * @param documentPublic Boolean indicating whether the document should be publicly accessible.
     * @param uri URI of the selected document file.
     * @param context Android context used to access content resolver.
     */
    fun onUpload(
        navController: NavController,
        documentName: String,
        documentContext: String,
        documentPublic: Boolean,
        uri: Uri,
        context: Context
    ) {
        val userId = userRepository.getUser()!!.googleId
        val errorPrefix = "Error: "
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: throw IOException("Could not open document")
                val fileBytes = inputStream.readBytes()
                inputStream.close()

                val fileName =
                    uri.lastPathSegment ?: "$userId-$documentName-${System.currentTimeMillis()}.pdf"

                val result = docRepository.uploadDocument(
                    fileBytes = fileBytes,
                    fileName = fileName,
                    userId = userId,
                    documentName = documentName,
                    documentContext = documentContext,
                    documentPublic = documentPublic
                )

                if (result) {
                    navController.navigate(Route.Home)
                } else {
                    showErrorToast(RuntimeException(),  "Upload failed", context)
                }
            } catch (e: IOException) {
                showErrorToast(e,  errorPrefix + e.message, context)
            } catch (e: ResponseException) {
                showErrorToast(e,  errorPrefix + e.message, context)
            } catch (e: SerializationException) {
                showErrorToast(e,  errorPrefix + e.message, context)
            }
        }
        _state.update { it.copy(name = documentName, context = documentContext, uri = uri) }
    }

    private suspend fun showErrorToast(e: Exception, errorMessage: String, context: Context) {
        withContext(Dispatchers.Main) {
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
        e.printStackTrace()
    }

    internal companion object {
        const val MAX_NAME_LENGTH = 50
        const val MAX_CONTEXT_LENGTH = 500
    }
}
