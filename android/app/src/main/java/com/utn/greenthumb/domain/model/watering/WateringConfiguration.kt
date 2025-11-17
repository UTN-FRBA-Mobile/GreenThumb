package com.utn.greenthumb.domain.model.watering

import com.google.gson.annotations.SerializedName

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

enum class DayOfWeek {
    monday, tuesday, wednesday, thursday, friday, saturday, sunday
}

sealed class WateringConfigurationDetailsDTO()

data class WateringScheduleDTO(
    val daysOfWeek: List<DayOfWeek>,
) : WateringConfigurationDetailsDTO()

data class WateringDatesDTO(
    val datesInterval: Int
) : WateringConfigurationDetailsDTO()