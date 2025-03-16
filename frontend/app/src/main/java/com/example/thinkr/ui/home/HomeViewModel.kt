package com.example.thinkr.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * ViewModel that manages the home screen state and document-related operations.
 *
 * Responsible for handling user interactions on the home screen, managing document retrieval,
 * suggested materials loading, and navigation to other screens.
 *
 * @property docRepository Repository for accessing and managing document data.
 * @property userRepository Repository for accessing user information.
 */
class HomeViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    var state: StateFlow<HomeScreenState> = _state.asStateFlow()

    /**
     * Processes user actions on the home screen.
     *
     * Handles navigation, document selection, dialog visibility, and file uploads.
     *
     * @param action The user action to process.
     * @param navController Navigation controller to handle screen transitions.
     */
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

    /**
     * Retrieves the user's documents from the repository.
     *
     * Updates the state with the retrieved documents. Will retry if user info is not yet available.
     */
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
        } else {
            delay(1000)
            getDocuments()
        }
    }

    /**
     * Retrieves suggested learning materials for the user.
     *
     * Updates the state with suggested materials. Handles various potential error conditions.
     */
    suspend fun getSuggestedMaterial() {
        if (userRepository.getUser() != null) {
            try {
                val suggestedMaterials = docRepository.getSuggestedMaterials(
                    userId = userRepository.getUser()!!.googleId,
                    limit = 1
                )
                _state.update { it.copy(suggestedMaterials = suggestedMaterials) }
            } catch (e: IOException) {
                Log.e(TAG, "Network error getting suggested materials", e)
                e.printStackTrace()
            } catch (e: ResponseException) {
                Log.e(TAG, "API error getting suggested materials", e)
                e.printStackTrace()
            } catch (e: SerializationException) {
                Log.e(TAG, "Parsing error getting suggested materials", e)
                e.printStackTrace()
            }
        }
    }

    /**
     * Signs out the current user by removing user data.
     */
    fun signOut() {
        userRepository.delUser()
    }

    /**
     * Tag for logging purposes.
     */
    companion object {
        private const val TAG = "HomeViewModel"
    }
}
