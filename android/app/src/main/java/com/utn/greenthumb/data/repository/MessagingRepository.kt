package com.utn.greenthumb.data.repository

import android.content.Context
import android.util.Log
import com.utn.greenthumb.client.services.PlantsApiService
import com.utn.greenthumb.utils.NotificationHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val plantsApi: PlantsApiService,
) {

    suspend fun refreshToken(): String? {
        val token = NotificationHelper.refreshToken(context)
        Log.d("Repository", token ?: "es null")

        if (token != null) {
            //plantsApi.updateNotificationToken(token)
        }

        return token

    }

}


