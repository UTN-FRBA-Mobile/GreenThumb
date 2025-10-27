package com.utn.greenthumb.domain.model.watering

import com.google.gson.annotations.SerializedName
import com.utn.greenthumb.domain.model.watering.WateringType.DATES_FREQUENCY
import com.utn.greenthumb.domain.model.watering.WateringType.SCHEDULES
import java.time.LocalTime

data class WateringConfigurationDTO(
    val id: String?,
    val plantId: String,
    val time: LocalTime,
    val details: WateringConfigurationDetailsDTO
)

enum class WateringType {
    @SerializedName("schedules")
    SCHEDULES,

    @SerializedName("dates-frequency")
    DATES_FREQUENCY
}

sealed class WateringConfigurationDetailsDTO {
    abstract val type: WateringType
}

data class WateringScheduleDTO(
    override val type: WateringType = SCHEDULES,
    val daysOfWeek: List<String>
) : WateringConfigurationDetailsDTO()

data class WateringDatesDTO(
    override val type: WateringType = DATES_FREQUENCY,
    val datesInterval: Int
) : WateringConfigurationDetailsDTO()