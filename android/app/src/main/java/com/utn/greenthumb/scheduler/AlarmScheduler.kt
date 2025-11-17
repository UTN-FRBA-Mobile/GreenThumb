package com.utn.greenthumb.scheduler

import com.utn.greenthumb.domain.model.watering.DayOfWeek
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO

interface AlarmScheduler {
    fun schedule(item: WateringConfigurationDTO, dayOfWeek: DayOfWeek)

    fun scheduleInterval(item: WateringConfigurationDTO, interval: Int)


    fun cancel(item: WateringConfigurationDTO)
}
