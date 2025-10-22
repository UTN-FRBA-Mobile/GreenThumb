package com.utn.greenthumb.viewmodel
import com.utn.greenthumb.domain.model.UserMessage
import com.utn.greenthumb.domain.model.Severity
import androidx.annotation.DrawableRes
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.R
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.data.repository.WateringReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject
/*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
*/
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Calendar


/*
// Compatible con API 26
fun stringToLocalDate(strDate: String): LocalDate {
    // 2. Analiza el String ISO 8601 directamente a un ZonedDateTime.
    //    No se necesita un formateador si es el formato estándar.
    val reminderZonedDateTime = ZonedDateTime.parse(strDate)

    // 3. Convierte el ZonedDateTime a la LocalDate del usuario para una comparación justa.
    return reminderZonedDateTime
        .withZoneSameInstant(ZoneId.systemDefault()) // Ajusta a la zona horaria del dispositivo
        .toLocalDate()
}

fun getDaysBetween(startDate: Date, endDate: Date): Long {
    val daysLeft = ChronoUnit.DAYS.between(todayLocalDate, reminderLocalDate)
}
*/

// Compatible con API 24
fun stringToLocalDate(strDate: String): Date { // Devuelve Date, no LocalDate
    // 1. Define el formato exacto del String ISO 8601 que esperas.
    //    'T' es un literal. 'Z' al final indica la zona horaria UTC.
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    isoFormat.timeZone = TimeZone.getTimeZone("UTC") // Fija la zona horaria a UTC

    try {
        // 2. Parsea el String a un objeto java.util.Date
        return isoFormat.parse(strDate) ?: Date() // Devuelve la fecha parseada o la actual si falla
    } catch (e: Exception) {
        // Maneja un posible error de parseo
        Log.e("DateParser", "Error parsing date: $strDate", e)
        return Date() // Devuelve la fecha actual como fallback
    }
}

fun getDaysBetween(startDate: Date, endDate: Date): Int {
    val startCal = Calendar.getInstance()
    startCal.time = startDate
    // Pone la hora a cero para comparar solo días
    startCal.set(Calendar.HOUR_OF_DAY, 0)
    startCal.set(Calendar.MINUTE, 0)
    startCal.set(Calendar.SECOND, 0)
    startCal.set(Calendar.MILLISECOND, 0)

    val endCal = Calendar.getInstance()
    endCal.time = endDate
    // Pone la hora a cero
    endCal.set(Calendar.HOUR_OF_DAY, 0)
    endCal.set(Calendar.MINUTE, 0)
    endCal.set(Calendar.SECOND, 0)
    endCal.set(Calendar.MILLISECOND, 0)

    val diffInMillis = endCal.timeInMillis - startCal.timeInMillis
    // Convierte milisegundos a días
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
}


@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val plantRepository: PlantRepository,
    private val wateringReminderRepository: WateringReminderRepository
) : ViewModel() {

    data class FavouritePlant(
        val id: String,
        val name: String,
        val imageUrl: String,
        @DrawableRes val imagePlaceholder: Int,
    )

    data class WateringReminder (
        val id: String,
        val plantId: String,
        val plantName: String,
        val plantImageUrl: String,
        @DrawableRes val plantImagePlaceholder: Int,
        val daysLeft: Int,
        val date: Date,
        val overdue: Boolean,
        val checked: Boolean
    )

    data class FavouritePlantsUIState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val favourites: List<FavouritePlant> = listOf(),
        val onSelectFavouritePlant: (String) -> Unit = {}
    )

    data class WateringScheduleUIState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val schedule: List<WateringReminder> = listOf(),
        val onCheckWateringReminder: (String) -> Unit = {}
    )

    data class HomeUIState(
        val isLoading: Boolean = false,
        val wateringScheduleUIState: WateringScheduleUIState = WateringScheduleUIState(),
        val favouritePlantsUIState: FavouritePlantsUIState = FavouritePlantsUIState()
    )

    private val _uiHomeState = MutableStateFlow(HomeUIState(
        isLoading = true
    ))
    val uiHomeState: StateFlow<HomeUIState> = _uiHomeState


    init {
        Log.d("HomeViewModel", "ViewModel ha sido INICIADO.")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("HomeViewModel", "ViewModel ha sido DESTRUIDO (onCleared). Limpiando recursos.")
    }

    fun fetchData(clientId: String) {
        try {
            if (clientId.isBlank()) {
                Log.e("HomeViewModel", "No client ID provided")
                return // No hacer nada si no hay ID
            }

            Log.d("HomeViewModel", "Fetching data for client: $clientId")

            viewModelScope.launch {

                val wateringScheduleUIState = fetchWateringSchedule(clientId)
                val favouritePlantsUIState = fetchFavouritePlants(clientId)

                Log.d("HomeViewModel", "Successfully fetched. data: $wateringScheduleUIState")

                _uiHomeState.update {
                    it.copy(
                        isLoading = false,
                        favouritePlantsUIState = favouritePlantsUIState,
                        wateringScheduleUIState = wateringScheduleUIState
                    )
                }
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching data", e)
        }
    }

    fun onCheckWateringReminder(reminderId: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Checking watering reminder: $reminderId")
                //wateringReminderRepository.checkWateringReminder(reminderId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error checking watering reminder", e)
            }
        }
    }

    fun onSelectFavouritePlant(plantId: String) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Selecting favourite plant: $plantId")
                //plantRepository.selectFavouritePlant(plantId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error selecting favourite plant", e)
            }
        }
    }

    private suspend fun fetchWateringSchedule(clientId: String): WateringScheduleUIState {
        try {
            Log.d("HomeViewModel", "Fetching watering schedule for client: $clientId")
            val result = wateringReminderRepository.getWateringReminders()
            Log.d("HomeViewModel", "Successfully fetched watering schedule. Data: ${result.toString()}")
            Log.d("HomeViewModel", "Successfully fetched ${result.total} watering reminders")

            return WateringScheduleUIState(
                isLoading = false,
                isValid = true,
                userMessages = listOf(),
                schedule = result.content.map { reminderDto ->
                    val reminderDate = stringToLocalDate(reminderDto.date)
                    val daysLeft = getDaysBetween(Date(), reminderDate)
                    val isOverdue = daysLeft < 0

                    WateringReminder(
                        id = reminderDto.id,
                        plantId = reminderDto.plantId,
                        plantName = reminderDto.plantName,
                        plantImageUrl = reminderDto.plantImageUrl,
                        plantImagePlaceholder = R.drawable.greenthumb,
                        daysLeft = daysLeft,
                        date = reminderDate,
                        overdue = isOverdue,
                        checked = reminderDto.checked
                    )
                }
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching watering schedule", e)

            if (e !is CancellationException) {
                return WateringScheduleUIState(
                    isLoading = false,
                    isValid = false,
                    userMessages = listOf(
                        UserMessage(
                            message = R.string.error_fetching_watering.toString(),
                            severity = Severity.ERROR
                        )
                    ),
                    schedule = listOf()
                )
            } else {
                Log.d("HomeViewModel", "Watering schedule fetch was cancelled.")

                return WateringScheduleUIState(
                    isLoading = false,
                    isValid = false,
                )
            }
        }
    }

    private suspend fun fetchFavouritePlants(clientId: String): FavouritePlantsUIState {
        try {
            Log.d("HomeViewModel", "Fetching favourite plants for client: $clientId")
            val result = plantRepository.getFavouritePlants()
            Log.d("HomeViewModel", "Successfully fetched favourite plants. Data: ${result.toString()}")
            Log.d("HomeViewModel", "Successfully fetched ${result.total} favourite plants")

            return FavouritePlantsUIState(
                isLoading = false,
                isValid = true,
                userMessages = listOf(),
                favourites = result.content.map { plant ->
                    FavouritePlant(
                        id = plant.id?: "",
                        name = plant.name,
                        imageUrl = plant.images?.firstOrNull()?.url ?: "",
                        imagePlaceholder = R.drawable.greenthumb,
                    )
                }
            )
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error fetching favourite plants", e)

            if (e !is CancellationException) {
                return FavouritePlantsUIState(
                    isLoading = false,
                    isValid = false,
                    userMessages = listOf(
                        UserMessage(
                            message = R.string.error_fetching_favourites.toString(),
                            severity = Severity.ERROR
                        )
                    ),
                    favourites = listOf()
                )
            } else {
                Log.d("HomeViewModel", "Favourite plants fetch was cancelled.")
                return FavouritePlantsUIState (
                    isLoading = false,
                    isValid = false,
                )
            }
        }
    }

}