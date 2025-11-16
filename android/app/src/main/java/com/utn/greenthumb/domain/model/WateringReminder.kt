package com.utn.greenthumb.domain.model


data class WateringReminderDTO (
    val id: String,
    val plantId: String,
    val userId: String,
    val plantName: String,
    val plantImageUrl: String,
    val date: String,
    val checked: Boolean
)