package com.example.miniapp.helper_classes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.miniapp.R
import com.example.miniapp.views.MainActivity

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    companion object {
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(task: String?): NotificationCompat.Builder {
        val openAppIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("To-do:")
                .setContentText(task)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_alarm_clock)
    }

    fun sendNotification(notificationID: Int, task: String?) {
        if (notificationID == 0) {
            log("Error in sending notification")
        }
        val notifyBuilder = getNotificationBuilder(task)
        notificationManager!!.notify(notificationID, notifyBuilder.build())
    }
}