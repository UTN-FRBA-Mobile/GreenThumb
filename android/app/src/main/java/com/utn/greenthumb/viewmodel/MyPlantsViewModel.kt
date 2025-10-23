package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.PlantDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
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

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    companion object {
        private const val FETCH_TIMEOUT = 30000L // 30 segundos
        private const val DELETE_TIMEOUT = 15000L // 15 segundos
    }


    /**
     * Obtener todas las plantas de un usuario
     */
    fun fetchMyPlants(clientId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("MyPlantsViewModel", "Fetching plants for client: $clientId")
                val result = withTimeout(FETCH_TIMEOUT) {
                    repository.getPlants()
                }

                _plants.value = result.content
                Log.d("MyPlantsViewModel", "Successfully fetched ${result.total} plants")
                Log.d("MyPlantsViewModel", "Plants fetched: $result")

            } catch (e: TimeoutCancellationException) {
                Log.e("MyPlantsViewModel", "Timeout fetching plants", e)
                _error.value = "La conexión tardó demasiado. Verifica tu internet e intenta nuevamente."
            } catch (e: SocketTimeoutException) {
                Log.e("MyPlantsViewModel", "Socket timeout fetching plants", e)
                _error.value = "Tiempo de espera agotado. Verifica tu conexión a internet."
            } catch (e: UnknownHostException) {
                Log.e("MyPlantsViewModel", "No internet connection", e)
                _error.value = "Sin conexión a internet. Verifica tu conexión."
            } catch (e: IOException) {
                Log.e("MyPlantsViewModel", "IO error fetching plants", e)
                _error.value = "Error de red. Verifica tu conexión a internet."
            } catch (e: Exception) {
                Log.e("MyPlantsViewModel", "Error fetching plants", e)
                _error.value = "Error al cargar plantas: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deletePlant(plantId: String) {
        viewModelScope.launch {
            try {
                _isDeleting.value = true
                _deleteError.value = null
                _deleteSuccess.value = false

                Log.d("MyPlantsViewModel", "Deleting plant: $plantId")

                withTimeout(DELETE_TIMEOUT) {
                    repository.deletePlant(plantId)
                }

                Log.d("MyPlantsViewModel", "Plant deleted successfully")

                _plants.value = _plants.value.filter { it.externalId != plantId }
                _deleteSuccess.value = true

            } catch (e: TimeoutCancellationException) {
                Log.e("MyPlantsViewModel", "Timeout deleting plant", e)
                _deleteError.value = "La operación tardó demasiado. Intenta nuevamente."
            } catch (e: SocketTimeoutException) {
                Log.e("MyPlantsViewModel", "Socket timeout deleting plant", e)
                _deleteError.value = "Tiempo de espera agotado. Verifica tu conexión."
            } catch (e: UnknownHostException) {
                Log.e("MyPlantsViewModel", "No internet connection while deleting", e)
                _deleteError.value = "Sin conexión a internet."
            } catch (e: IOException) {
                Log.e("MyPlantsViewModel", "IO error deleting plant", e)
                _deleteError.value = "Error de red al eliminar la planta."
            } catch (e: Exception) {
                Log.e("MyPlantsViewModel", "Error deleting plant", e)
                _deleteError.value = "Error al eliminar: ${e.localizedMessage ?: "Error desconocido"}"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun resetDeleteState() {
        _deleteError.value = null
        _deleteSuccess.value = false
    }

    fun clearError() {
        _error.value = null
    }
}