package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val repository: PlantRepository
) : BaseViewModel<List<Plant>>() {


    fun identifyPlant(request: IdentificationRequest) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // TODO: Solamente para mostrar la pantalla de Loading
            delay(3000)

            try {
                Log.d("PlantViewModel", "Sending image to PlantAPI")
                val result = repository.identifyPlant(request)
                if (result.isNotEmpty()) {
                    Log.d("PlantViewModel", "Plants identified: $result")
                    _uiState.value = UiState.Success(result)
                } else {
                    Log.d("PlantViewModel", "No plants found")
                    _uiState.value = UiState.Error("No se encontraron resultados.")
                }
            } catch (e: Exception) {
                Log.e("PlantViewModel", "Error identificando planta", e)
                _uiState.value = UiState.Error("Error: ${e.localizedMessage}")            }
        }
    }

    fun clearResults() {
        Log.d("PlantViewModel", "Results cleared")
        _uiState.value = UiState.Idle
    }
}
