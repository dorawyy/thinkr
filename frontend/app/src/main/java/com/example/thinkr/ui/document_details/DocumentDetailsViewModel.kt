package com.example.thinkr.ui.document_details

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.repositories.DocRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DocumentDetailsViewModel(private val documentRepositoryImpl: DocRepositoryImpl) : ViewModel() {
    private val _state = MutableStateFlow(DocumentDetailsState())

    fun onBackPressed(navController: NavController) {
        navController.navigate(Route.Home)
    }

    fun onUpload(navController: NavController, name: String, context: String, uri: Uri) {
        _state.update { it.copy(name = name, context = context, uri = uri) }
        documentRepositoryImpl.uploadDocument(name, uri)
        navController.navigate(Route.Home)
    }
}
