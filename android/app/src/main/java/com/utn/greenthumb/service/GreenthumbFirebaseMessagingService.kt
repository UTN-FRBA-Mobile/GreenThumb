package com.utn.greenthumb.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class GreenthumbFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}