package com.utn.greenthumb.domain.model
import java.util.Date

data class WateringReminderDTO (
    val id: String,
    val plantId: String,
    val plantName: String,
    val plantImageUrl: String,
    val date: Date,
    val checked: Boolean
)