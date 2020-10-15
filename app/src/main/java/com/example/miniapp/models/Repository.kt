package com.example.miniapp.models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.miniapp.helper_classes.*
import java.util.*

/*
* Roles of the repo:
* 1. feed data to custom adapter and alarm service
* 2. listens for db changes from listener service
* */

object Repository : IPublisher<Task> {
    private var adapter: ISubscriber<Task>? = null
    private var alarmService: ISubscriber<Task>? = null
    private val taskList = mutableListOf<Task>()

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction("INTENT_ACTION_DB_CHANGED")
        context.registerReceiver(changesReceiver, intentFilter)
    }

    private val changesReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                when (it.action) {
                    "INTENT_ACTION_DB_CHANGED" -> {
                        val task : Task = intent.getBundleExtra("taskExtra")?.getSerializable("taskObj") as Task
                        taskList.addUnique(task)
                    }
                }
            }
        }
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(changesReceiver)
    }

    override fun addSub(subscriber: ISubscriber<Task>) {
        when (subscriber) {
            is CustomAdapter -> {
                adapter = subscriber
                log("adapter is subbed")
            }
            is AlarmService -> {
                alarmService = subscriber
                log("alarm is subbed")
            }
        }
    }

    /*
    *   TODO: when deletion becomes possible, this method should instead merge changes
    *       from the database with the taskList
    */
    private fun MutableList<Task>.addUnique(task: Task){
        var isUnique = true
        this.forEach {
            if (it.isSame(task)) {
                isUnique = false
            }
        }

        if(isUnique){
            this.add(task)
            log("ADDED FROM SERVICE: Task Retrieved: ${task.task}")
            log("ADDED FROM SERVICE: Created Retrieved: ${task.dateCreated}")
            log("ADDED FROM SERVICE: Start Retrieved: ${task.dateStart}")
            log("ADDED FROM SERVICE: URI Retrieved: ${task.imageURI}")
            log("ADDED FROM SERVICE: Address Retrieved: ${task.address}")
            notifySubs(task)
        }
    }

    override fun notifySubs(notifyInput: Task) {
        adapter?.update(notifyInput)

        if (notifyInput.dateStart.after(Calendar.getInstance().time)) {
            alarmService?.update(notifyInput)
        }
    }

    fun onRequestNotify() {
        taskList.forEach { notifySubs(it) }
    }
}
