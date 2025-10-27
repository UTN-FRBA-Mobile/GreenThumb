package com.utn.greenthumb.viewmodel;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.data.repository.WateringConfigurationRepository
import com.utn.greenthumb.domain.model.PlantCatalogDTO
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfigsUIState(
    val isLoading: Boolean = false,
    val rememberConfigurations: List<WateringConfigurationDTO> = listOf(),
)

data class ModalUIState(
    val visible: Boolean = false,
    val loadingPlants: Boolean = false,
    val plantNames: List<PlantCatalogDTO> = listOf(),
)

@HiltViewModel
class WateringConfigViewModel @Inject constructor(
    private val repository: WateringConfigurationRepository,
    private val plantRepository: PlantRepository,
) : ViewModel() {

    private val _configsState = MutableStateFlow(
        ConfigsUIState(
            isLoading = true
        )
    )
    val configurations = _configsState

    private val _modalState = MutableStateFlow(
        ModalUIState(
            visible = false
        )
    )

    val modalState = _modalState

    fun getConfigs() {
        viewModelScope.launch {
            try {
                val response = repository.getConfigurations()

                _configsState.update {
                    it.copy(
                        isLoading = false,
                        rememberConfigurations = response.content
                    )
                }
            } catch (e: Exception) {
                _configsState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }

        }
    }

    fun openModal() {
        _modalState.update {
            it.copy(
                visible = true,
                loadingPlants = true
            )
        }

        viewModelScope.launch {
            val catalog = plantRepository.getPlantCatalog()

            _modalState.update {
                it.copy(
                    plantNames = catalog,
                    loadingPlants = false
                )
            }
        }
    }

    fun closeModal() {
        _modalState.update {
            it.copy(
                visible = false
            )
        }
    }

}
