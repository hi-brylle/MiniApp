package com.example.miniapp.helper_classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.sendNotification(intent.getIntExtra("notificationID", 0), intent.getStringExtra("task"))
    }
}