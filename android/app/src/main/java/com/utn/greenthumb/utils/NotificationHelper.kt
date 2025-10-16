package com.utn.greenthumb.utils

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

object NotificationHelper {

    fun hasNotificationPermission(context: Context): Boolean {
        return (SDK_INT < TIRAMISU) || checkSelfPermission(
            context,
            POST_NOTIFICATIONS
        ) == PERMISSION_GRANTED
    }

    fun refreshToken(context: Context): String? {
        if (!hasNotificationPermission(context)) {
            return null
        }
        return FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            task.result
        }).result
    }
}