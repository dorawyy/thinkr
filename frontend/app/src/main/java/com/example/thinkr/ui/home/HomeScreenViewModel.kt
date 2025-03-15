package com.example.thinkr.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.thinkr.app.Route
import com.example.thinkr.data.models.User
import com.example.thinkr.data.repositories.auth.AuthRepository
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class HomeScreenViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
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

    fun checkUser(account: GoogleSignInAccount?) {
        if (account == null && userRepository.getUser() == null) {
            Log.w("HomeScreenViewModel", "User not signed in")
        } else if (userRepository.getUser() == null) {
            viewModelScope.launch {
                authRepository.login(
                    googleId = account!!.id!!,
                    name = account.displayName ?: "",
                    email = account.email ?: ""
                ).fold(
                    onSuccess = {
                        userRepository.setUser(it.data.user.copy())
                    },
                    onFailure = { exception ->
                        Log.e("HomeScreenViewModel", "Error logging in", exception)
                        userRepository.setUser(
                            User(
                                email = account.email ?: "",
                                name = account.displayName ?: "",
                                googleId = account.id ?: "",
                                subscribed = false
                            )
                        )
                    }
                )
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
        } else {
            delay(1000)
            getDocuments()
        }
    }

    suspend fun getSuggestedMaterial() {
        if (userRepository.getUser() != null) {
            try {
                val suggestedMaterials = docRepository.getSuggestedMaterials(
                    userId = userRepository.getUser()!!.googleId,
                    limit = 1
                )
                _state.update { it.copy(suggestedMaterials = suggestedMaterials) }
            } catch (e: IOException) {
                Log.e("HomeScreenViewModel", "Network error getting suggested materials", e)
                e.printStackTrace()
            } catch (e: ResponseException) {
                Log.e("HomeScreenViewModel", "API error getting suggested materials", e)
                e.printStackTrace()
            } catch (e: SerializationException) {
                Log.e("HomeScreenViewModel", "Parsing error getting suggested materials", e)
                e.printStackTrace()
            }
        }
    }

    fun signOut() {
        userRepository.delUser()
    }
}
