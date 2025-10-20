package com.utn.greenthumb.event

import com.utn.greenthumb.domain.model.PlantDTO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NavigationEventsBus {
    private val _events = MutableSharedFlow<NavigationEvent>()
    val events = _events.asSharedFlow()

    suspend fun emit(event: NavigationEvent) {
        _events.emit(event)
    }
}

sealed class NavigationEvent {
    data class ToPlantDetail(val plant: PlantDTO) : NavigationEvent()
}
