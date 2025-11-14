package com.utn.greenthumb.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import com.utn.greenthumb.receiver.WateringAlarmReceiver
import java.util.Calendar

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: WateringConfigurationDTO) {
        val intent = Intent(context, WateringAlarmReceiver::class.java).apply {
            putExtra("PLANT_NAME", item.plantName)
            putExtra("REMINDER_ID", item.id?.toInt() ?: 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id?.toInt() ?: 0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            val timeParts = item.time.split(":")
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    override fun cancel(item: WateringConfigurationDTO) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id?.toInt() ?: 0,
            Intent(context, WateringAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}