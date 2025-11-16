package com.utn.greenthumb.scheduler

import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO

interface AlarmScheduler {
    fun schedule(item: WateringConfigurationDTO)
    fun cancel(item: WateringConfigurationDTO)
}
