package com.utn.greenthumb.data.model.watering

import com.google.gson.annotations.SerializedName

data class WateringReminderCheckRequest (
    @SerializedName("checked") val checked: Boolean
)