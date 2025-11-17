package com.utn.greenthumb.viewmodel
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.R
import com.utn.greenthumb.data.repository.PlantRepository
import com.utn.greenthumb.data.repository.WateringReminderRepository
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.Severity
import com.utn.greenthumb.domain.model.UserMessage
import com.utn.greenthumb.domain.model.WateringDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.util.UUID



fun stringToLocalDate(strDate: String): Date { // Devuelve Date, no LocalDate
    val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    isoFormat.timeZone = TimeZone.getTimeZone("UTC")

    try {
        return isoFormat.parse(strDate) ?: Date()
    } catch (e: Exception) {
        Log.e("DateParser", "Error parsing date: $strDate", e)
        return Date()
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

    companion object {
        private const val FETCH_TIMEOUT = 30000L // 30 segundos
        private const val CHECK_TIMEOUT = 15000L // 15 segundos
    }

    data class FavouritePlant(
        val id: String,
        val name: String,
        val imageUrl: String,
        val plant: PlantDTO,
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
        val onCheck: Boolean,
        val watering: WateringDTO?
    )

    data class FavouritePlantsUIState(
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val favourites: List<FavouritePlant> = listOf(),
    )

    data class WateringScheduleUIState(
        val isValid: Boolean = false,
        val userMessages: List<UserMessage> = listOf(),
        val schedule: List<WateringReminder> = listOf(),
        val onCheckWateringReminder: (WateringReminder) -> Unit = {}
    )

    data class HomeUIState(
        val isLoading: Boolean = false,
        val isValid: Boolean = false,
        val userMessage: UserMessage,
        val wateringScheduleUIState: WateringScheduleUIState = WateringScheduleUIState(),
        val favouritePlantsUIState: FavouritePlantsUIState = FavouritePlantsUIState()
    )

    private val _uiHomeState = MutableStateFlow(HomeUIState(
        isLoading = true,
        isValid = false,
        userMessage = UserMessage(
            message = "",
            severity = Severity.INFO
        )
    ))
    val uiHomeState: StateFlow<HomeUIState> = _uiHomeState


    private fun setOnCheckWateringReminder(reminder: WateringReminder) {
        try {
            _uiHomeState.update { currentState ->
                val updatedSchedule = currentState.wateringScheduleUIState.schedule.map {
                    if (it.id == reminder.id) {
                        it.copy(
                            onCheck = !it.onCheck,
                        )
                    } else {
                        it
                    }
                }

                currentState.copy(
                    wateringScheduleUIState = currentState.wateringScheduleUIState.copy(
                        schedule = updatedSchedule
                    )
                )
            }
        }
        catch (e: Exception) {
            Log.e("HomeViewModel", "Error setting onCheckWateringReminder", e)
        }
    }

    fun onCheckWateringReminder(reminder: WateringReminder) {
        try {
            Log.d("HomeViewModel", "Checking watering reminder: ${reminder.id}")

            if (reminder.onCheck) {
                Log.d("HomeViewModel", "Double Checking watering reminder: ${reminder.id}")
                // es un doble click del usuario. la operacion ya se esta realizando
                return
            }

            setOnCheckWateringReminder(reminder)

            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e("HomeViewModel", "Error checking watering reminder", exception)
                val msg = when (exception) {
                    is IOException -> R.string.home_error_no_internet.toString()
                    else -> R.string.home_error_checking_watering_reminder.toString()
                }

                Log.e("HomeViewModel", "Error checking watering reminder: $msg")

                _uiHomeState.update {
                    it.copy(
                        userMessage = UserMessage(
                            id = UUID.randomUUID().toString(),
                            message = msg,
                            severity = Severity.ERROR,
                            showToast = true
                        )
                    )
                }

                setOnCheckWateringReminder(reminder)
            }

            viewModelScope.launch(Dispatchers.IO + handler) {
                    Log.d("HomeViewModel", "Checking watering reminder: ${reminder.id}")

                    withTimeout(CHECK_TIMEOUT) {
                        wateringReminderRepository.checkWateringReminder(reminder.id)
                    }

                    _uiHomeState.update { currentState ->
                        val updatedSchedule = currentState.wateringScheduleUIState.schedule.filter {
                                it -> it.id != reminder.id
                        }

                        currentState.copy(
                            wateringScheduleUIState = currentState.wateringScheduleUIState.copy(
                                schedule = updatedSchedule
                            )
                        )
                    }
            }

        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error checking watering reminder", e)
            setOnCheckWateringReminder(reminder)
        }
    }

    fun fetchData(clientId: String) {
        try {
            _uiHomeState.update {
                it.copy(isLoading = true)
            }

            Log.d("HomeViewModel", "Fetching data for client: $clientId")

            val handler = CoroutineExceptionHandler { _, exception ->
                Log.e("HomeViewModel", "Error fetching data", exception)

                val userMessage = when (exception) {
                    is IOException ->
                        UserMessage(
                            id = UUID.randomUUID().toString(),
                            message = R.string.home_error_no_internet.toString(),
                            severity = Severity.ERROR,
                            showToast = true)
                    else ->
                        UserMessage(
                            message = R.string.home_error_fetching_data.toString(),
                            severity = Severity.ERROR)
                }

                _uiHomeState.update {
                    it.copy(
                        isLoading = false,
                        isValid = false,
                        userMessage = userMessage
                    )
                }
            }

            viewModelScope.launch(handler) {
                if (clientId.isBlank()) {
                    Log.e("HomeViewModel", "No client ID provided")
                    _uiHomeState.update {
                        it.copy(
                            isLoading = false,
                            isValid = false,
                            userMessage = UserMessage(
                                message = R.string.home_error_no_client_id.toString(),
                                severity = Severity.ERROR
                            )
                        )
                    }
                    return@launch
                }

                Log.d("HomeViewModel", "Fetching data for client: $clientId")
                val favouritePlantsDeferred = async(Dispatchers.IO) { fetchFavouritePlants(clientId) }
                val wateringScheduleDeferred = async(Dispatchers.IO) { fetchWateringSchedule(clientId) }

                val favouritePlantsUIState = favouritePlantsDeferred.await()
                val wateringScheduleUIState = wateringScheduleDeferred.await()

                Log.d("HomeViewModel", "Successfully fetched all data.")

                _uiHomeState.update {
                    it.copy(
                        isLoading = false,
                        isValid = true,
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

    private suspend fun fetchWateringSchedule(clientId: String): WateringScheduleUIState {
        Log.d("HomeViewModel", "Fetching watering schedule for client: $clientId")

        val result = withTimeout(FETCH_TIMEOUT) {
            wateringReminderRepository.getWateringReminders()
        }

        Log.d("HomeViewModel", "Successfully fetched ${result.total} watering reminders")

        return WateringScheduleUIState(
            isValid = true,
            userMessages = listOf(),
            onCheckWateringReminder = this::onCheckWateringReminder,
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
                    onCheck = false,
                    watering = reminderDto.watering
                )
            }
        )
    }

    private suspend fun fetchFavouritePlants(clientId: String): FavouritePlantsUIState {

        Log.d("HomeViewModel", "Fetching favourite plants for client: $clientId")

        val result = withTimeout(FETCH_TIMEOUT) {
            plantRepository.getFavouritePlants()
        }

        Log.d("HomeViewModel", "Successfully fetched ${result.total} favourite plants")

        return FavouritePlantsUIState(
            //isLoading = false,
            isValid = true,
            userMessages = listOf(),
            //onSelectFavouritePlant = this::onSelectFavouritePlant,
            favourites = result.content.map { plant ->
                FavouritePlant(
                    id = plant.id?: "",
                    name = plant.name,
                    imageUrl = plant.images?.firstOrNull()?.url ?: "",
                    plant = plant,
                    imagePlaceholder = R.drawable.greenthumb,
                )
            }
        )
    }
}