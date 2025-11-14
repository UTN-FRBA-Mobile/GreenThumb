package com.utn.greenthumb.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // DO NOTHING FOR NOW. This is a temporary change to diagnose the startup crash.
        Log.d("BootCompletedReceiver", "Receiver triggered, but doing nothing.")
    }
}