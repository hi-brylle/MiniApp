package com.example.miniapp.models

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.miniapp.helper_classes.log

/*
* Roles of the repo:
* 1. feeds data to custom adapter and alarm service
* 2. listens for db changes
* */
class Repository : BroadcastReceiver() {
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