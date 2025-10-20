package com.utn.greenthumb.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.utn.greenthumb.MainActivity
import com.utn.greenthumb.R
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.data.repository.AuthRepository
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.UserTokenDTO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GreenthumbFirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        private const val TAG: String = "GreenthumbFirebaseMessagingService"
    }

    @Inject
    lateinit var apiService: PlantsApiService
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onNewToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (authRepository.isUserLoggedIn()) {
                try {
                    apiService.updateNotificationToken(UserTokenDTO(token))
                } catch (e: Exception) {
                    Log.e(TAG, "Cannot update firebase token", e)
                }
            }
        }
    }

    @RequiresPermission(POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        if ("remember" == message.data.getOrDefault("type", "")) {
            val data = message.data
            val title = data["title"]
            val body = data["body"]
            val plant = data["plant"]?.let { Gson().fromJson(it, PlantDTO::class.java) }

            if (plant != null) {
                showNotification(title, body, plant)
            }
        }
    }
    @RequiresPermission(POST_NOTIFICATIONS)
    fun Context.showNotification(title: String?, body: String?, plant: PlantDTO) {
        val uniqueSeed = plant.externalId.hashCode()
        val notificationId = (System.currentTimeMillis() + uniqueSeed).toInt()

        val channelId = createChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
            data = "$channelId://$notificationId".toUri()
            putExtra("plant", plant)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, notificationId, intent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )

        val uniquePerson = Person.Builder()
            .setName(plant.name)
            .setKey("plant_id_${plant.id}_$notificationId") // Clave aún más única
            .build()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.photo_camera)
            .setContentTitle(title ?: "GreenThumb")
            .setContentText(body ?: "Notificación nueva")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup("unique_group_$notificationId")
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
            .setGroupSummary(false)
            .setOnlyAlertOnce(false)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify("unique_group_$notificationId", notificationId, notification)
        }
    }

    private fun createChannel(): String {
        val channelId = "default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Canal de notificaciones de GreenThumb"
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                enableLights(true)
                enableVibration(true)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        return channelId
    }
}