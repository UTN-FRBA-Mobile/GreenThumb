package com.utn.greenthumb.domain.model.watering

import com.google.gson.annotations.SerializedName
import java.util.Calendar

data class WateringConfigurationDTO(
    val plantId: String,
    val plantName: String? = null,
    val time: String,
    val details: WateringConfigurationDetailsDTO
) {
    @SerializedName("_id", alternate = ["id"]) var id: String? = null
}

enum class WateringType {
    @SerializedName("schedules")
    SCHEDULES,

    @SerializedName("dates-frequency")
    DATES_FREQUENCY
}

enum class DayOfWeek(val calendarDay: Int) {
    monday(Calendar.MONDAY),
    tuesday(Calendar.TUESDAY),
    wednesday(Calendar.WEDNESDAY),
    thursday(Calendar.THURSDAY),
    friday(Calendar.FRIDAY),
    saturday(Calendar.SATURDAY),
    sunday(Calendar.SUNDAY)
}

sealed class WateringConfigurationDetailsDTO()

data class WateringScheduleDTO(
    val daysOfWeek: List<DayOfWeek>,
) : WateringConfigurationDetailsDTO()

data class WateringDatesDTO(
    val datesInterval: Int
) : WateringConfigurationDetailsDTO()