package com.utn.greenthumb.viewmodel
import com.utn.greenthumb.domain.model.WateringReminderDTO
import com.utn.greenthumb.domain.model.UserMessage
import com.utn.greenthumb.domain.model.Severity

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.R
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.domain.model.PlantDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit


@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val repository: PlantRepository
) : ViewModel() {

    data class Plant(
        val id: String,
        val name: String,
        val imageUrl: String,
    )

    data class WateringReminder (
        val id: String,
        val plantId: String,
        val plantName: String,
        val plantImageUrl: String,
        val daysLeft: Int,
        val date: Date,
        val overdue: Boolean,
        val checked: Boolean
    )

    data class FavouritePlantsUIState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val favourites: List<Plant> = listOf(),
    )

    data class WateringScheduleUIState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val schedule: List<WateringReminder> = listOf(),
    )

    private val _uiFavouritePlantState = MutableStateFlow(FavouritePlantsUIState())
    val uiFavouritePlantState: StateFlow<FavouritePlantsUIState> = _uiFavouritePlantState

    private val _uiWateringScheduleState = MutableStateFlow(WateringScheduleUIState())
    val uiWateringScheduleState: StateFlow<WateringScheduleUIState> = _uiWateringScheduleState


    fun fetchWateringSchedule(clientId: String) {
        _uiWateringScheduleState.value = WateringScheduleUIState(
            isLoading = true,
            isValid = false,
            userMessages = listOf(),
            schedule = listOf()
        )

        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching watering schedule for client: $clientId")

                val result = repository.getWateringReminders()

                _uiWateringScheduleState.value = WateringScheduleUIState(
                    isLoading = false,
                    isValid = true,
                    userMessages = listOf(),
                    schedule = result.content.map { reminder ->

                        WateringReminder(
                            id = reminder.id,
                            plantId = reminder.plantId,
                            plantName = reminder.plantName,
                            plantImageUrl = reminder.plantImageUrl,
                            daysLeft = 0,       // TODO
                            date = reminder.date,
                            overdue = false,    // TODO
                            checked = reminder.checked
                        )
                    }
                )

                Log.d("HomeViewModel", "Successfully fetched ${result.total} watering schedule")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching watering schedule", e)

                _uiWateringScheduleState.value = WateringScheduleUIState(
                    isLoading = false,
                    isValid = false,
                    userMessages = listOf(UserMessage(
                        message = R.string.error_fetching_favourites.toString(),
                        severity = Severity.ERROR)),
                    schedule = listOf()
                )
            }
        }
    }

    fun fetchFavouritePlants(clientId: String) {
        _uiFavouritePlantState.value = FavouritePlantsUIState(
            isLoading = true,
            isValid = false,
            userMessages = listOf(),
            favourites = listOf()
        )

        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Fetching favourite plants for client: $clientId")

                val result = repository.getFavouritePlants()

                _uiFavouritePlantState.value = FavouritePlantsUIState(
                    isLoading = false,
                    isValid = true,
                    userMessages = listOf(),
                    favourites = result.content.map { plant ->
                        Plant(
                            id = plant.id?: "",
                            name = plant.name,
                            imageUrl = plant.images?.firstOrNull()?.url ?: "",
                        )
                    }
                )

                Log.d("HomeViewModel", "Successfully fetched ${result.total} favourite plants")

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching favourite plants", e)

                _uiFavouritePlantState.value = FavouritePlantsUIState(
                    isLoading = false,
                    isValid = false,
                    userMessages = listOf(UserMessage(
                        message = R.string.error_fetching_watering.toString(),
                        severity = Severity.ERROR
                    )),
                    favourites = listOf()
                )
            }
        }
    }
}