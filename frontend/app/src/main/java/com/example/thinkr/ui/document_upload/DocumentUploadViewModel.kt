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

class DocumentUploadViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DocumentUploadState())

    fun onBackPressed(navController: NavController) {
        navController.navigate(Route.Home)
    }

    fun onUploadWithBytes(
        navController: NavController,
        documentName: String,
        documentContext: String,
        fileBytes: ByteArray,
        fileName: String,
        context: Context
    ) {
        val userId = userRepository.getUser()!!.googleId
        viewModelScope.launch {
            try {
                val result = docRepository.uploadDocument(
                    fileBytes = fileBytes,
                    fileName = fileName,
                    userId = userId,
                    documentName = documentName,
                    documentContext = documentContext
                )

                if (result) {
                    navController.navigate(Route.Home)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Upload failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            } catch (e: ResponseException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            } catch (e: SerializationException) {
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
    }

    fun onUpload(
        navController: NavController,
        documentName: String,
        documentContext: String,
        uri: Uri,
        context: Context
    ) {
        val userId = userRepository.getUser()!!.googleId
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
                    documentContext = documentContext
                )

                if (result) {
                    navController.navigate(Route.Home)
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "Upload failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            } catch (e: ResponseException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                e.printStackTrace()
            } catch (e: SerializationException) {
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
