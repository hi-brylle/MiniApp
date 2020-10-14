package com.example.miniapp.models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.example.miniapp.helper_classes.IPublisher
import com.example.miniapp.helper_classes.ISubscriber
import com.example.miniapp.helper_classes.log

/*
* Roles of the repo:
* 1. feeds data to custom adapter and alarm service
* 2. listens for db changes from service
* */

object Repository : IPublisher<Task> {
    private val changesReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.action == "INTENT_ACTION_DB_CHANGED"){
                    val task : Task = intent.getBundleExtra("taskExtra")?.getSerializable("taskObj") as Task
                    log("FROM SERVICE: Task Retrieved: ${task.task}")
                    log("FROM SERVICE: Created Retrieved: ${task.dateCreated}")
                    log("FROM SERVICE: Start Retrieved: ${task.dateStart}")
                    log("FROM SERVICE: URI Retrieved: ${task.imageURI}")
                    log("FROM SERVICE: Address Retrieved: ${task.address}")
                }
            }
        }
    }

    init {

    }

    fun register(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction("INTENT_ACTION_DB_CHANGED")
        context.registerReceiver(changesReceiver, intentFilter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(changesReceiver)
    }

    override fun addSub(subscriber: ISubscriber<Task>?) {
        TODO("Not yet implemented")
    }

    override fun notifySubs(notifyInput: Task) {
        TODO("Not yet implemented")
    }


}