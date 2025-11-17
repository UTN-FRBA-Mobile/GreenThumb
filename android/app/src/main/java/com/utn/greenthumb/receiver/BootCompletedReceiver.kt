package com.utn.greenthumb.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.utn.greenthumb.data.repository.WateringConfigurationRepository
import com.utn.greenthumb.domain.model.watering.WateringDatesDTO
import com.utn.greenthumb.domain.model.watering.WateringScheduleDTO
import com.utn.greenthumb.scheduler.AlarmScheduler
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootCompletedReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootCompletedReceiverEntryPoint {
        fun wateringConfigurationRepository(): WateringConfigurationRepository
        fun alarmScheduler(): AlarmScheduler
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val appContext = context.applicationContext ?: throw IllegalStateException()
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                appContext,
                BootCompletedReceiverEntryPoint::class.java
            )

            val repository = hiltEntryPoint.wateringConfigurationRepository()
            val scheduler = hiltEntryPoint.alarmScheduler()

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // This call is now safe because the repository uses an in-memory list
                    val configurations = repository.getConfigurations().content
                    configurations.forEach { config ->
                        if(config.details is WateringScheduleDTO){
                            for(day in config.details.daysOfWeek) {
                                scheduler.schedule(config,day)
                            }
                        }else{
                            scheduler.scheduleInterval(config,(config.details as WateringDatesDTO).datesInterval)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}