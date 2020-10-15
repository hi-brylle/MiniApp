package com.example.miniapp.models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.miniapp.helper_classes.*

/*
* Roles of the repo:
* 1. feeds data to custom adapter and alarm service
* 2. listens for db changes from service
* */

object Repository : IPublisher<Task> {
    private lateinit var adapter: ISubscriber<Task>
    private lateinit var alarmService: ISubscriber<Task>
    private val taskList = mutableListOf<Task>()

    private val changesReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.action == "INTENT_ACTION_DB_CHANGED"){
                    val task : Task = intent.getBundleExtra("taskExtra")?.getSerializable("taskObj") as Task
                    taskList.addUnique(task)
                }
            }
        }
    }

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction("INTENT_ACTION_DB_CHANGED")
        context.registerReceiver(changesReceiver, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(changesReceiver)
    }

    override fun addSub(subscriber: ISubscriber<Task>) {
        when (subscriber) {
            is CustomAdapter -> {
                adapter = subscriber
                log("adapter is in boisssss")
            }
            is AlarmService -> {
                alarmService = subscriber
                log("alarm is in boisssss")
            }
        }
    }

    fun MutableList<Task>.addUnique(task: Task){
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
        }
    }

    override fun notifySubs(notifyInput: Task) {
        adapter.update(notifyInput)
    }


}