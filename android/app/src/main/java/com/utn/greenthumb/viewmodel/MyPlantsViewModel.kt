package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.PlantDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlantsViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _plants = MutableStateFlow<List<PlantDTO>>(emptyList())
    val plants: StateFlow<List<PlantDTO>> = _plants

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchMyPlants(clientId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("MyPlantsViewModel", "Fetching plants for client: $clientId")
                val result = repository.getPlants()
                _plants.value = result.content
                Log.d("MyPlantsViewModel", "Successfully fetched ${result.total} plants")
            } catch (e: Exception) {
                Log.e("MyPlantsViewModel", "Error fetching plants", e)
                _error.value = when (e) {
                    is java.net.UnknownHostException -> "Sin conexión a internet"
                    is java.net.SocketTimeoutException -> "Tiempo de espera agotado"
                    else -> "Error al cargar plantas: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}