package com.utn.greenthumb.data.model.watering

import com.google.gson.annotations.SerializedName
import com.utn.greenthumb.domain.model.WateringDTO

data class WateringReminderResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("plantId") val plantId: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("plantName") val plantName: String,
    @SerializedName("plantImageUrl") val plantImageUrl: String,
    @SerializedName("date") val date: String, // Formato ISO 8601
    @SerializedName("checked") val checked: Boolean,
    @SerializedName("watering") val watering: WateringDTO?
)