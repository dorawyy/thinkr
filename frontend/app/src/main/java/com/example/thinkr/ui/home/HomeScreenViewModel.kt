package com.example.thinkr.ui.home

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeScreenViewModel(private val docRepository: DocRepository, private val userRepository: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    var state: StateFlow<HomeScreenState> = _state.asStateFlow()

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
        if (userRepository.getUser() != null) {
            _state.update {
                it.copy(
                    retrievedDocuments = docRepository.getDocuments(
                        userId = userRepository.getUser()!!.googleId,
                        documentIds = null
                    )
                )
            }
        }
    }
}
