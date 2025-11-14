package com.utn.greenthumb.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.utn.greenthumb.R

class WateringAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "your plant"
        val message = "It's time to water $plantName"
        val channelId = "watering_channel"
        val reminderId = intent.getIntExtra("REMINDER_ID", 0)

        val notification = NotificationCompat.Builder(context, channelId)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Watering Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId, notification)
    }
}