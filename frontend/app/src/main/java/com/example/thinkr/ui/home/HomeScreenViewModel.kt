package com.example.thinkr.ui.home

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.repositories.doc.DocRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel(private val docRepository: DocRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    var state: StateFlow<HomeScreenState> = _state.asStateFlow()

    init {
        // TODO: Remove, for demo only
        _state.update {
            it.copy(
                retrievedDocuments = listOf(
                    Document("1", "Document 1", "1", false, false),
                )
            )
        }
    }

    fun onAction(action: HomeScreenAction, navController: NavController) {
        when (action) {
            HomeScreenAction.BackButtonClicked -> {
                navController.navigate(Route.Landing)
            }

            HomeScreenAction.ProfileButtonClicked -> {
                navController.navigate(Route.Profile)
            }

            is HomeScreenAction.DocumentItemClicked -> {
                navController.navigate(Route.DocumentOptions.createRoute(action.documentItem))
            }

            HomeScreenAction.AddButtonClicked -> {
                // Handle add button click action
                _state.value = _state.value.copy(showDialog = true)
            }

            HomeScreenAction.DismissDialog -> {
                // Handle dismiss dialog action
                _state.value = _state.value.copy(showDialog = false)
            }

            is HomeScreenAction.FileSelected -> {
                // Handle file selected action
                navController.navigate(Route.DocumentUpload.createRoute(action.selectedUri))
            }
        }
    }

    suspend fun getDocuments() {
        docRepository.getDocuments(
            userId = "69", // TODO: change this
            documentIds = null
        )
    }
}
