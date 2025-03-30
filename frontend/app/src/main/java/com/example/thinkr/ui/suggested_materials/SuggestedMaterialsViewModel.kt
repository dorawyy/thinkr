package com.example.thinkr.ui.suggested_materials

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.thinkr.data.repositories.doc.DocRepository
import com.example.thinkr.data.repositories.user.UserRepository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * ViewModel for managing the state and operations related to suggested learning materials.
 *
 * @property docRepository Repository for accessing document-related data.
 * @property userRepository Repository for accessing user-related data.
 */
class SuggestedMaterialsViewModel(
    private val docRepository: DocRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow(SuggestedMaterialsState())
    var state: StateFlow<SuggestedMaterialsState> = _state.asStateFlow()

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
        _state.update { it.copy(isLoading = false) }
    }

    companion object {
        private const val TAG = "SuggestedMaterialsViewModel"
    }
}
