package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.Plant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlantsViewModel @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    private val _plants = MutableStateFlow<List<Plant>>(emptyList())
    val plants: StateFlow<List<Plant>> = _plants

    fun fetchMyPlants(clientId: String) {
        viewModelScope.launch {
            try {
                Log.d("MyPlantsViewModel", "Fetching plants for client: $clientId")
                val result = repository.getPlants()
                _plants.value = result.content
                Log.d("MyPlantsViewModel", "Successfully fetched ${result.total} plants")
            } catch (e: Exception) {
                Log.e("MyPlantsViewModel", "Error fetching plants", e)
            }
        }
    }
}