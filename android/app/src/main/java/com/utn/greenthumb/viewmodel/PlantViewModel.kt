package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository
) : BaseViewModel<List<PlantDTO>>() {

    private var isIdentifying = false

    private val _selectedPlant = MutableStateFlow<PlantDTO?>(null)
    val selectedPlant: StateFlow<PlantDTO?> = _selectedPlant.asStateFlow()


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


    fun selectPlant(plant: PlantDTO) {
        Log.d("PlantViewModel", "Plant selected: ${plant.name}")
        _selectedPlant.value = plant
    }


    fun clearSelectedPlant() {
        Log.d("PlantViewModel", "Selected plant cleared")
        _selectedPlant.value = null
    }


    fun savePlant(plant: PlantDTO) {
        viewModelScope.launch {
            repository.save(plant)
        }
    }


    fun clearResults() {
        Log.d("PlantViewModel", "Results cleared")
        _uiState.value = UiState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        clearSelectedPlant()
    }
}
