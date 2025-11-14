package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.data.repository.WateringConfigurationRepository
import com.utn.greenthumb.domain.model.PlantCatalogDTO
import com.utn.greenthumb.domain.model.watering.DayOfWeek
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import com.utn.greenthumb.domain.model.watering.WateringDatesDTO
import com.utn.greenthumb.domain.model.watering.WateringScheduleDTO
import com.utn.greenthumb.domain.model.watering.WateringType
import com.utn.greenthumb.domain.model.watering.WateringType.DATES_FREQUENCY
import com.utn.greenthumb.domain.model.watering.WateringType.SCHEDULES
import com.utn.greenthumb.scheduler.AlarmScheduler
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
    val loadingPlants: Boolean? = false,
    val plantNames: List<PlantCatalogDTO>? = listOf(),
    val selectedConfig: RememberModalForm = RememberModalForm(),
    val editFlow: Boolean = false
)

data class RememberModalForm(
    val id: String? = null,
    val selectedPlant: PlantCatalogDTO? = null,
    val type: WateringType = SCHEDULES,
    val time: String = "",
    val numberInput: Int? = null,
    val selectedDays: List<DayOfWeek> = mutableListOf(),
) {
    fun isValid(): Boolean {
        return selectedPlant != null && !time.isEmpty() && (
                (SCHEDULES == type && !selectedDays.isEmpty()) ||
                        (DATES_FREQUENCY == type && (numberInput ?: 0) > 0)
                )
    }
}

@HiltViewModel
class WateringConfigViewModel @Inject constructor(
    private val repository: WateringConfigurationRepository,
    private val plantRepository: PlantRepository,
    private val scheduler: AlarmScheduler
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
            fetchConfigs()
        }
    }

    private suspend fun fetchConfigs() {
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

    fun openModal() {
        _modalState.update {
            it.copy(
                visible = true,
                editFlow = false,
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
                editFlow = false,
                visible = false,
                selectedConfig = RememberModalForm()
            )
        }
    }

    fun create(form: RememberModalForm) {
        _configsState.update {
            it.copy(
                isLoading = true,
            )
        }
        val details = when (form.type) {
            SCHEDULES -> WateringScheduleDTO(daysOfWeek = form.selectedDays)
            DATES_FREQUENCY -> WateringDatesDTO(datesInterval = form.numberInput ?: 0)
        }
        viewModelScope.launch {
            try {
                val newWateringConfiguration = repository.create(
                    WateringConfigurationDTO(
                        plantId = form.selectedPlant?.id ?: "",
                        time = form.time,
                        details = details,
                        id = null
                    )
                )
                newWateringConfiguration.let {
                    scheduler.schedule(        WateringConfigurationDTO(
                        plantId = form.selectedPlant?.id ?: "",
                        time = form.time,
                        details = details,
                        id = null
                    ))
                }

                fetchConfigs()
            } catch (e: Exception) {
                Log.e("WateringConfigViewModel", "Error creating configuration", e)
                _configsState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun delete(reminder: WateringConfigurationDTO) {
        _configsState.update {
            it.copy(
                isLoading = true,
            )
        }
        viewModelScope.launch {
            try {
                scheduler.cancel(reminder)
                repository.delete(reminder)

                fetchConfigs()
            } catch (e: Exception) {
                Log.e("WateringConfigViewModel", "Error deleting configuration", e)
                _configsState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun update(form: RememberModalForm) {
        _configsState.update {
            it.copy(
                isLoading = true,
            )
        }
        val details = when (form.type) {
            SCHEDULES -> WateringScheduleDTO(daysOfWeek = form.selectedDays)
            DATES_FREQUENCY -> WateringDatesDTO(datesInterval = form.numberInput ?: 0)
        }
        viewModelScope.launch {
            try {
                val updatedWateringConfiguration = WateringConfigurationDTO(
                    id = form.id,
                    plantId = form.selectedPlant?.id ?: "",
                    plantName = form.selectedPlant?.name,
                    time = form.time,
                    details = details,
                )
                repository.update(
                    updatedWateringConfiguration
                )
                scheduler.schedule(updatedWateringConfiguration)

                fetchConfigs()
            } catch (e: Exception) {
                Log.e("WateringConfigViewModel", "Error updating configuration", e)
                _configsState.update {
                    it.copy(
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun openModalForEdit(confifguration: WateringConfigurationDTO) {

        val selectedConfig = if (confifguration.details is WateringScheduleDTO)  {
            RememberModalForm(
                id = confifguration.id,
                time = confifguration.time,
                selectedPlant = PlantCatalogDTO(confifguration.plantId, confifguration.plantName ?: ""),
                type = SCHEDULES,
                selectedDays = confifguration.details.daysOfWeek,
            )
        } else if (confifguration.details is WateringDatesDTO) {
            RememberModalForm(
                id = confifguration.id,
                time = confifguration.time,
                selectedPlant = PlantCatalogDTO(confifguration.plantId, confifguration.plantName ?: ""),
                type = DATES_FREQUENCY,
                numberInput = confifguration.details.datesInterval,
            )
        } else {
            RememberModalForm()
        }

        _modalState.update {
            it.copy(
                visible = true,
                loadingPlants = false,
                editFlow = true,
                selectedConfig = selectedConfig
            )
        }
    }

}
