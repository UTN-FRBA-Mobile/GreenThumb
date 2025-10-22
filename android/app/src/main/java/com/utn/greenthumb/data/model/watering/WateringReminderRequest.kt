package com.utn.greenthumb.data.model.watering

import com.google.gson.annotations.SerializedName

import java.util.Date

data class WateringReminderRequest (
    @SerializedName("plantId") val plantId: String,
    @SerializedName("plantName") val plantName: String,
    @SerializedName("plantImageUrl") val plantImageUrl: String,
    @SerializedName("date") val date: String,
    @SerializedName("checked") val checked: Boolean
)
