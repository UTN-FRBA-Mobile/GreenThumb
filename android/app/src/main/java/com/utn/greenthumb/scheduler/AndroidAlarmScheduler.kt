package com.utn.greenthumb.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.utn.greenthumb.domain.model.watering.DayOfWeek
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import com.utn.greenthumb.receiver.WateringAlarmReceiver
import java.util.Calendar

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: WateringConfigurationDTO, dayOfWeek: DayOfWeek) {
        val intent = Intent(context, WateringAlarmReceiver::class.java).apply {
            putExtra("PLANT_NAME", item.plantName)
            putExtra("REMINDER_ID", item.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
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
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek.calendarDay)
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        );

    }

    override fun scheduleInterval(item: WateringConfigurationDTO, interval: Int) {
        val intent = Intent(context, WateringAlarmReceiver::class.java).apply {
            putExtra("PLANT_NAME", item.plantName)
            putExtra("REMINDER_ID", item.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
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
        calendar.set(Calendar.DAY_OF_WEEK, interval)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * interval,
            pendingIntent
        );


    }

    override fun cancel(item: WateringConfigurationDTO) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.hashCode(),
            Intent(context, WateringAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}