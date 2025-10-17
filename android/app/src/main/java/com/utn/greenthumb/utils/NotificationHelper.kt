package com.utn.greenthumb.utils

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object NotificationHelper {

    fun hasNotificationPermission(context: Context): Boolean {
        return (SDK_INT < TIRAMISU) || checkSelfPermission(
            context,
            POST_NOTIFICATIONS
        ) == PERMISSION_GRANTED
    }

    suspend fun refreshToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.d("NotificationHelper", "Error getting token", e)
            null
        }

    }
}