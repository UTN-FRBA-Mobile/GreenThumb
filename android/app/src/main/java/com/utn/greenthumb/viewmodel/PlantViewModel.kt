package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import kotlinx.coroutines.withContext
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository
) : BaseViewModel<List<Plant>>() {

    private var isIdentifying = false

    fun identifyPlant(request: IdentificationRequest) {

        if (isIdentifying) {
            Log.d("PlantViewModel", "Already identifying...")
            return
        }

        viewModelScope.launch {
            try {
                isIdentifying = true
                _uiState.value = UiState.Loading

                Log.d("PlantViewModel", "Sending image to PlantAPI")
                val result = withContext(Dispatchers.IO) {
                    repository.identifyPlant(request)
                }

                if (result.isNotEmpty()) {
                    Log.d("PlantViewModel", "Plants identified: $result")
                    _uiState.value = UiState.Success(result)
                } else {
                    Log.d("PlantViewModel", "No plants found")
                    _uiState.value = UiState.Error("No se encontraron resultados.")
                }
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Error identificando planta", e)
                _uiState.value = UiState.Error("Error: ${e.localizedMessage}")
            } finally {
                isIdentifying = false
            }

        }
    }

    fun clearResults() {
        Log.d("PlantViewModel", "Results cleared")
        _uiState.value = UiState.Idle
    }
}
