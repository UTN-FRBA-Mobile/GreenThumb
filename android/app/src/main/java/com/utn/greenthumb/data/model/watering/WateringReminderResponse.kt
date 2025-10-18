package com.utn.greenthumb.data.model.watering

import java.util.Date

data class WateringReminderResponse (
    val id: String,
    val plantId: String,
    val plantName: String,
    val plantImageUrl: String,
    val date: Date,
    val checked: Boolean
)