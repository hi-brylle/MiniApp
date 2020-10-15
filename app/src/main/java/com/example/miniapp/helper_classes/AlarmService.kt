package com.example.miniapp.helper_classes

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Pair
import android.widget.Toast
import com.example.miniapp.models.Repository
import com.example.miniapp.models.Task
import kotlin.collections.ArrayList

class AlarmService : Service(), ISubscriber<Task> {
    private lateinit var activeTasks: ArrayList<Pair<Int, String>>

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        cancelAll()
        log("ALARM SERVICE STOPPED")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.getStringExtra("email")?.let {
            log("ALARM SERVICE STARTED")
            Repository.register(this)
            start(it)
        } ?: run {log("NULL EMAIL. SERVICE FAILED TO START")}

        return START_STICKY
    }

    override fun stopService(name: Intent): Boolean {
        return super.stopService(name)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Toast.makeText(this, "App Killed. All alarms are disabled", Toast.LENGTH_SHORT).show()
    }

    private fun start(emailFromSP: String) {
        log("start service for user $emailFromSP")
    }

    override fun update(updateInput: Task) {
        val task = updateInput.task
        val unixTimestamp = updateInput.dateStart.time
        val notificationID = (unixTimestamp / 1000).toInt()

        if (unixTimestamp == 0L || notificationID == 0) {
            log("warning: default zero values on alarm service params")
        } else {
            recordActiveTask(task, notificationID)
            setAlarm(task, unixTimestamp, notificationID)
        }
    }

    private fun recordActiveTask(task: String, notificationID: Int) {
        activeTasks.add(Pair(notificationID, task))
    }

    private fun setAlarm(task: String, unixTimestamp: Long, notificationID: Int) {
        val intent = Intent(applicationContext, SendNotificationReceiver::class.java)
        intent.putExtra("task", task)
        intent.putExtra("notificationID", notificationID)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, notificationID, intent, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.RTC_WAKEUP, unixTimestamp] = pendingIntent
        log("alarm set for $task at $unixTimestamp")
    }

    private fun cancelAlarm(notificationID: Int, task: String) {
        val intent = Intent(applicationContext, SendNotificationReceiver::class.java)
        intent.putExtra("task", task)
        intent.putExtra("notificationID", notificationID)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, notificationID, intent, PendingIntent.FLAG_ONE_SHOT)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            log("cancelled alarm for $task with ID $notificationID")
        }
    }

    private fun cancelAll() {
        for (pair in activeTasks) {
            cancelAlarm(pair.first, pair.second)
        }
        activeTasks.clear()
        log("cancelled all alarms for user")
    }
}